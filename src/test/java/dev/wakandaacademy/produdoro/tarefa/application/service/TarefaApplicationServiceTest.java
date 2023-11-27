package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

        // Assert
        assertEquals(tarefas.size(), result.size());
    }
}