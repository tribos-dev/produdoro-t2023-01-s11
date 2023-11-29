package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.Value;

@Value
public class TarefaListResponse {
	private UUID idTarefa;
	private String descricao;
	private StatusTarefa status;
	private StatusAtivacaoTarefa statusAtivacao;
	private int contagemPomodoro;
	
	public static List<TarefaListResponse> converte(List<Tarefa> tarefas) {		
		return tarefas.stream()
				.map(TarefaListResponse::new)
				 .collect(Collectors.toList());
	}
	
	public TarefaListResponse(Tarefa tarefa) {		
		this.idTarefa = tarefa.getIdTarefa();
		this.descricao = tarefa.getDescricao();
		this.status = tarefa.getStatus();
		this.statusAtivacao = tarefa.getStatusAtivacao();
		this.contagemPomodoro = tarefa.getContagemPomodoro();
	}

}
