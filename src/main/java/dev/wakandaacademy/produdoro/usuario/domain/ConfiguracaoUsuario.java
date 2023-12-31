 package dev.wakandaacademy.produdoro.usuario.domain;

import dev.wakandaacademy.produdoro.pomodoro.domain.ConfiguracaoPadrao;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfiguracaoUsuario {
	private Integer tempoMinutosFoco;
	private Integer tempoMinutosPausaCurta;
	private Integer tempoMinutosPausaLonga;
	private Integer repeticoesParaPausaLonga;

	public ConfiguracaoUsuario(ConfiguracaoPadrao configuracaoPadrao) {
		this.tempoMinutosFoco = configuracaoPadrao.getTempoMinutosFoco();
		this.tempoMinutosPausaCurta = configuracaoPadrao.getTempoMinutosPausaCurta();
		this.tempoMinutosPausaLonga = configuracaoPadrao.getTempoMinutosPausaLonga();
		this.repeticoesParaPausaLonga = configuracaoPadrao.getRepeticoesParaPausaLonga();
	}
}
