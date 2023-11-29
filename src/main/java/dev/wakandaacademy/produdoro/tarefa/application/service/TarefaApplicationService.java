package dev.wakandaacademy.produdoro.tarefa.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class TarefaApplicationService implements TarefaService {
	private final TarefaRepository tarefaRepository;
	private final UsuarioRepository usuarioRepository;

	@Override
	public TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest) {
		log.info("[inicia] TarefaApplicationService - criaNovaTarefa");
		Tarefa tarefaCriada = tarefaRepository.salva(new Tarefa(tarefaRequest));
		log.info("[finaliza] TarefaApplicationService - criaNovaTarefa");
		return TarefaIdResponse.builder().idTarefa(tarefaCriada.getIdTarefa()).build();
	}

	@Override
	public Tarefa detalhaTarefa(String usuario, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - detalhaTarefa");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		log.info("[usuarioPorEmail] {}", usuarioPorEmail);
		Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
				.orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada!"));
		tarefa.pertenceAoUsuario(usuarioPorEmail);
		log.info("[finaliza] TarefaApplicationService - detalhaTarefa");
		return tarefa;
	}

	@Override
	public void ativaTarefa(UUID idTarefa, UUID idUsuario, String usuarioEmail) {
		log.info("[inicia] TarefaApplicationService - ativaTarefa");
		Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(usuarioEmail);
		Tarefa tarefa = detalhaTarefa(usuarioEmail, idTarefa);
		tarefaRepository.desativaTarefasAtivas(usuario.getIdUsuario());
		tarefa.defineStatusAtivacao(idUsuario);
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - ativaTarefa");
	}

	@Override
	public void editaTarefa(String usuario, UUID idTarefa, EditaTarefaRequest tarefaRequest) {
		log.info("[inicia] TarefaApplicationService = editaTarefa");
		Tarefa tarefa = detalhaTarefa(usuario, idTarefa);
		tarefa.edita(tarefaRequest);
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService = editaTarefa");
	}

	@Override
	public void incrementaPomodoroAoUsuario(String emailUsuario, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - incrementaPomodoroAoUsuario");
		Tarefa tarefa = detalhaTarefa(emailUsuario, idTarefa);
		tarefa.contagemPomodoro();
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - incrementaPomodoroAoUsuario");
	}

	@Override
	public List<TarefaListResponse> buscaTarefasPorUsuario(String usuario, UUID idUsuario) {
		log.info("[inicia] TarefaApplicationService - buscaTarefasPorUsuario");
		usuarioRepository.buscaUsuarioPorId(idUsuario);
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		usuarioPorEmail.validaUsuario(idUsuario);
		List<Tarefa> listaTarefas = tarefaRepository.buscaTarefasPorUsuario(idUsuario);
		log.info("[finaliza] TarefaApplicationService - buscaTarefasPorUsuario");
		return TarefaListResponse.converte(listaTarefas);
	}

	@Override
	public void concluiTarefa(UUID idTarefa, UUID idUsuario, String usuario) {
		log.info("[inicia] TarefaApplicationService - concluiTarefa");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		log.info("[usuarioPorEmail] {}", usuarioPorEmail);
		Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
				.orElseThrow(() -> APIException.build(HttpStatus.BAD_REQUEST, "Tarefa não encontrada!"));
		tarefa.pertenceAoUsuario(usuarioPorEmail);
		tarefa.concluiTarefa();
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - concluiTarefa");

	}

	@Override
	public void deletaTarefa(String usuario, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - deletarTarefa");
		Tarefa tarefa = detalhaTarefa(usuario, idTarefa);
		tarefaRepository.deletaTarefa(tarefa);
		log.info("[finaliza] TarefaApplicationService - deletarTarefa");
	}

}
