package dev.wakandaacademy.produdoro.usuario.domain;

import java.util.UUID;

import javax.validation.constraints.Email;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.pomodoro.domain.ConfiguracaoPadrao;
import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioNovoRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@Document(collection = "Usuario")
public class Usuario {
	@Id
	private UUID idUsuario;
	@Email
	@Indexed(unique = true)
	private String email;
	private ConfiguracaoUsuario configuracao;
	@Builder.Default
	private StatusUsuario status = StatusUsuario.FOCO;
	@Builder.Default
	private Integer quantidadePomodorosPausaCurta = 0;

	public Usuario(UsuarioNovoRequest usuarioNovo, ConfiguracaoPadrao configuracaoPadrao) {
		this.idUsuario = UUID.randomUUID();
		this.email = usuarioNovo.getEmail();
		this.status = StatusUsuario.FOCO;
		this.configuracao = new ConfiguracaoUsuario(configuracaoPadrao);
	}

	public void mudaStatusParaPausaCurta(UUID idUsuario) {
		validaUsuario(idUsuario);
		this.status = StatusUsuario.PAUSA_CURTA;
	}

	public void mudaStatusParaPausaLonga(UUID idUsuario) {
		validaUsuario(idUsuario);
		this.status = StatusUsuario.PAUSA_LONGA;
	}

	public void validaUsuario(UUID idUsuario) {
		log.info("[inicia] Usuario - validaUsuario");
		if (!this.idUsuario.equals(idUsuario)) {
			throw APIException.build(HttpStatus.UNAUTHORIZED, "Credencial de autenticação não é válida");

		}
		log.info("[finaliza] Usuario - validaUsuario");

	}

	public void atualizaStatusFoco(UUID idUsuario) {
		log.info("[inicia] Usuario - atualizaStatusFoco");
		validaUsuario(idUsuario);
		verificaStatusAtual();
		log.info("[finaliza] Usuario - atualizaStatusFoco");

	}

	private void verificaStatusAtual() {
		if (this.status.equals(StatusUsuario.FOCO)) {
			throw APIException.build(HttpStatus.CONFLICT, "O status usuário já está como foco");

		}
		mudaStatusParaFoco();
	}

	private void mudaStatusParaFoco() {
		this.status = StatusUsuario.FOCO;

	}
}

