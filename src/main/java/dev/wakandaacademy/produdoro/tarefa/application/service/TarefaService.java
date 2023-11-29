package dev.wakandaacademy.produdoro.tarefa.application.service;

import java.util.List;
import java.util.UUID;

import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

public interface TarefaService {
	TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);

	Tarefa detalhaTarefa(String usuario, UUID idTarefa);

	void ativaTarefa(UUID idTarefa, UUID idUsuario, String usuarioEmail);

	void concluiTarefa(UUID idTarefa, UUID idUsuario, String usuario);

	void editaTarefa(String usuario, UUID idTarefa, EditaTarefaRequest tarefaRequest);

	List<TarefaListResponse> buscaTarefasPorUsuario(String usuario, UUID idUsuario);

	void incrementaPomodoroAoUsuario(String emailUsuario, UUID idTarefa);

	void deletaTarefa(String usuario, UUID idTarefa);
}
