package dev.wakandaacademy.produdoro.tarefa.application.service;

import static dev.wakandaacademy.produdoro.DataHelper.ID_TAREFA_VALIDO;
import static dev.wakandaacademy.produdoro.DataHelper.TOKEN_VALIDO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TarefaApplicationServiceTest {

    //	@Autowired
    @InjectMocks
    TarefaApplicationService tarefaApplicationService;

    //	@MockBean
    @Mock
    TarefaRepository tarefaRepository;
    @Mock
    UsuarioRepository usuarioRepository;
    private Optional<Tarefa> OptionalTarefaValidaEsperada;
    private Usuario usuarioValido;
    private TarefaRequest TarefaRequest;
    private EditaTarefaRequest editaTarefaRequest;

    @Mock
    UsuarioRepository usuarioRepository;

    @Test
    void deveRetornarIdTarefaNovaCriada() {
        TarefaRequest request = getTarefaRequest();
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request));

        TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

        assertNotNull(response);
        assertEquals(TarefaIdResponse.class, response.getClass());
        assertEquals(UUID.class, response.getIdTarefa().getClass());
    }


    @Test
    public void testConcluiTarefa() {
        String usuario = "email@email.com";
        UUID idTarefa = UUID.fromString("06fb5521-9d5a-461a-82fb-e67e3bedc6eb");
        Usuario usuarioMock = mock(Usuario.class);
        Tarefa tarefaMock = mock(Tarefa.class);
        when(usuarioRepository.buscaUsuarioPorEmail(usuario)).thenReturn(usuarioMock);
        when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.of(tarefaMock));
        tarefaApplicationService.concluiTarefa(idTarefa,UUID.randomUUID(),usuario);
        verify(usuarioRepository).buscaUsuarioPorEmail(usuario);
        verify(tarefaRepository).buscaTarefaPorId(idTarefa);
        verify(tarefaMock).pertenceAoUsuario(usuarioMock);
        verify(tarefaMock).concluiTarefa();
        verify(tarefaRepository).salva(tarefaMock);
}


    public TarefaRequest getTarefaRequest() {
        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
        return request;
    }

    @Test
    void testEditaTarefa() {
        EditaTarefaRequest request = getEditaTarefaRequest();
        String email = "gabriel123@gmail.com";
        UUID idTarefa = UUID.randomUUID();
        Tarefa tarefa = mock(Tarefa.class);
        when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.of(tarefa));
        tarefaApplicationService.editaTarefa(email, idTarefa, request);
        verify(tarefaRepository, times(1)).salva(tarefa);

    }
    public EditaTarefaRequest getEditaTarefaRequest() {
        return new EditaTarefaRequest("tarefa 1");
    }
    void deveDeletarTarefaComSucesso(){
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();

        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(tarefa.getIdTarefa())).thenReturn(Optional.of(tarefa));

        tarefaApplicationService.deletaTarefa(usuario.getEmail(),tarefa.getIdTarefa());
        verify(tarefaRepository,times(1)).deletaTarefa(tarefa);
    }
    @Test
    void deveNaoDeletarTarefa_QuandoPassarIdDaTarefaInvalido() {
        UUID idTarefa = UUID.fromString("06fb5521-9d5a-461a-82fb-e67e3bedc6e3");
        String usuarioEmail = "juliana@gmail.com";
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();

        APIException ex = assertThrows(APIException.class, () ->
            tarefaApplicationService.deletaTarefa(usuario.getEmail(), tarefa.getIdTarefa())
        );

        assertNotEquals(idTarefa, tarefa.getIdTarefa());
        assertNotEquals(usuarioEmail, usuario.getEmail());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
    }
}
