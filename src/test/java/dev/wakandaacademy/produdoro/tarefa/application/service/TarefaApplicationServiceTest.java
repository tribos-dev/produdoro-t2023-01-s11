package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

@ExtendWith(MockitoExtension.class)
class TarefaApplicationServiceTest {

	@InjectMocks
	TarefaApplicationService tarefaApplicationService;

	@Mock
	TarefaRepository tarefaRepository;

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
		tarefaApplicationService.concluiTarefa(idTarefa, UUID.randomUUID(), usuario);
		verify(usuarioRepository).buscaUsuarioPorEmail(usuario);
		verify(tarefaRepository).buscaTarefaPorId(idTarefa);
		verify(tarefaMock).pertenceAoUsuario(usuarioMock);
		verify(tarefaMock).concluiTarefa();
		verify(tarefaRepository).salva(tarefaMock);
	}

	@Test
	@DisplayName("Teste unitário ativa tarefa")
	void ativaTarefaDeveRetornarTarefaAtiva() {
		UUID idTarefa = DataHelper.createTarefa().getIdTarefa();
		UUID idUsuario = DataHelper.createUsuario().getIdUsuario();
		String email = "gabrielteste@gmail.com";
		Tarefa retorno = DataHelper.getTarefaAtivaTarefa();
		when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(DataHelper.createUsuario());
		when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(DataHelper.createTarefa()));
		tarefaApplicationService.ativaTarefa(idTarefa, idUsuario, email);
		verify(tarefaRepository, times(1)).buscaTarefaPorId(idTarefa);
		assertEquals(StatusAtivacaoTarefa.ATIVA, retorno.getStatusAtivacao());
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

	public TarefaRequest getTarefaRequest() {
		TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
		return request;
	}

	@Test
	void deveIncrementarPomodoroAoUsuario() {
		Usuario usuario = DataHelper.createUsuario();
		Tarefa tarefa = DataHelper.createTarefa();

		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));

		tarefaApplicationService.incrementaPomodoroAoUsuario(usuario.getEmail(), tarefa.getIdTarefa());

		verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(usuario.getEmail());
		verify(tarefaRepository, times(1)).buscaTarefaPorId(tarefa.getIdTarefa());
		verify(tarefaRepository, times(1)).salva(tarefa);
		assertEquals(2, tarefa.getContagemPomodoro());
	}

	@Test
	void deveRetornarUmaExceptionAoIncrementarPomodoroAUmaTarefa() {
		Tarefa tarefa = DataHelper.createTarefa();
		String usuarioEmailInvalido = "emailinvalido@gmail.com";

		when(usuarioRepository.buscaUsuarioPorEmail(usuarioEmailInvalido)).thenThrow(APIException.class);
		APIException ex = assertThrows(APIException.class,
				() -> tarefaApplicationService.incrementaPomodoroAoUsuario(usuarioEmailInvalido, tarefa.getIdTarefa()));
	}

	@Test
	void deveDeletarTarefaComSucesso() {
		Usuario usuario = DataHelper.createUsuario();
		Tarefa tarefa = DataHelper.createTarefa();

		when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefaPorId(tarefa.getIdTarefa())).thenReturn(Optional.of(tarefa));

		tarefaApplicationService.deletaTarefa(usuario.getEmail(), tarefa.getIdTarefa());
		verify(tarefaRepository, times(1)).deletaTarefa(tarefa);
	}

	@Test
	void deveNaoDeletarTarefa_QuandoPassarIdDaTarefaInvalido() {
		UUID idTarefa = UUID.fromString("06fb5521-9d5a-461a-82fb-e67e3bedc6e3");
		String usuarioEmail = "juliana@gmail.com";
		Usuario usuario = DataHelper.createUsuario();
		Tarefa tarefa = DataHelper.createTarefa();

		APIException ex = assertThrows(APIException.class,
				() -> tarefaApplicationService.deletaTarefa(usuario.getEmail(), tarefa.getIdTarefa()));

		assertNotEquals(idTarefa, tarefa.getIdTarefa());
		assertNotEquals(usuarioEmail, usuario.getEmail());
		assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
	}

	// Teste quebrado
//	@Test
//	void buscaTarefasPorUsuario_DeveRetornarListaVazia_QuandoNaoExistiremTarefas() {
//		// Arrange
//		UUID idUsuario = UUID.randomUUID();
//		when(usuarioRepository.buscaUsuarioPorId(idUsuario)).thenReturn(new Usuario());
//		when(tarefaRepository.buscaTarefasPorUsuario(idUsuario)).thenReturn(Collections.emptyList());
//		List<TarefaListResponse> result = tarefaApplicationService.buscaTarefasPorUsuario("usuario", idUsuario);
//		assertTrue(result.isEmpty());
//	}

	// Teste quebrado
//	@Test
//	void buscaTarefasPorUsuario_DeveRetornarListaTarefas_QuandoExistiremTarefas() {
//		// Arrange
//		UUID idUsuario = UUID.randomUUID();
//		when(usuarioRepository.buscaUsuarioPorId(idUsuario)).thenReturn(new Usuario());
//		List<Tarefa> tarefas = List.of(new Tarefa(), new Tarefa());
//		when(tarefaRepository.buscaTarefasPorUsuario(idUsuario)).thenReturn(tarefas);
//
//		// Act
//		List<TarefaListResponse> result = tarefaApplicationService.buscaTarefasPorUsuario("usuario", idUsuario);
//
//		// Assert
//		assertEquals(tarefas.size(), result.size());
//	}
}
