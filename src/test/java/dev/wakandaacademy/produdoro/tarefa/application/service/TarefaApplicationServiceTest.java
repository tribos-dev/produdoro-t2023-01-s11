package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

class TarefaApplicationServiceTest {

    @InjectMocks
    private TarefaApplicationService tarefaService;

    @Mock
    private TarefaRepository tarefaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void buscaTarefasPorUsuario_DeveRetornarListaVazia_QuandoNaoExistiremTarefas() {
        // Arrange
        UUID idUsuario = UUID.randomUUID();
        when(usuarioRepository.buscaUsuarioPorId(idUsuario)).thenReturn(new Usuario());
        when(tarefaRepository.buscaTarefasPorUsuario(idUsuario)).thenReturn(Collections.emptyList());

        // Act
        List<TarefaListResponse> result = tarefaService.buscaTarefasPorUsuario("usuario", idUsuario);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void buscaTarefasPorUsuario_DeveRetornarListaTarefas_QuandoExistiremTarefas() {
        // Arrange
        UUID idUsuario = UUID.randomUUID();
        when(usuarioRepository.buscaUsuarioPorId(idUsuario)).thenReturn(new Usuario());
        List<Tarefa> tarefas = List.of(new Tarefa(), new Tarefa());
        when(tarefaRepository.buscaTarefasPorUsuario(idUsuario)).thenReturn(tarefas);

        // Act
        List<TarefaListResponse> result = tarefaService.buscaTarefasPorUsuario("usuario", idUsuario);

    }
    
     @Test
    public void testConcluiTarefa() {
        String usuario = "email@email.com";
        UUID idTarefa = UUID.fromString("06fb5521-9d5a-461a-82fb-e67e3bedc6eb");
        Usuario usuarioMock = mock(Usuario.class);
        Tarefa tarefaMock = mock(Tarefa.class);
        when(usuarioRepository.buscaUsuarioPorEmail(usuario)).thenReturn(usuarioMock);
        when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.of(tarefaMock));
        tarefaService.concluiTarefa(idTarefa,UUID.randomUUID(),usuario);
        verify(usuarioRepository).buscaUsuarioPorEmail(usuario);
        verify(tarefaRepository).buscaTarefaPorId(idTarefa);
        verify(tarefaMock).pertenceAoUsuario(usuarioMock);
        verify(tarefaMock).concluiTarefa();
        verify(tarefaRepository).salva(tarefaMock);

        // Assert
        assertEquals(tarefas.size(), result.size());
    }


    @Test
    void deveDeletarTarefaComSucesso(){
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();

        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(tarefa.getIdTarefa())).thenReturn(Optional.of(tarefa));

        tarefaService.deletaTarefa(usuario.getEmail(),tarefa.getIdTarefa());
        verify(tarefaRepository,times(1)).deletaTarefa(tarefa);
    }
    @Test
    void deveNaoDeletarTarefa_QuandoPassarIdDaTarefaInvalido() {
        UUID idTarefa = UUID.fromString("06fb5521-9d5a-461a-82fb-e67e3bedc6e3");
        String usuarioEmail = "juliana@gmail.com";
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();

        APIException ex = assertThrows(APIException.class, () ->
        tarefaService.deletaTarefa(usuario.getEmail(), tarefa.getIdTarefa())
        );

        assertNotEquals(idTarefa, tarefa.getIdTarefa());
        assertNotEquals(usuarioEmail, usuario.getEmail());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
    }
}

