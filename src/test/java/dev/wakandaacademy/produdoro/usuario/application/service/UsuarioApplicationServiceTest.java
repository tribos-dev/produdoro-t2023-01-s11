package dev.wakandaacademy.produdoro.usuario.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.credencial.application.service.CredencialService;
import dev.wakandaacademy.produdoro.pomodoro.application.service.PomodoroService;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

@ExtendWith(MockitoExtension.class)
class UsuarioApplicationServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @InjectMocks
    private UsuarioApplicationService usuarioApplicationService;
       

    @Test
	public void MudaStatusParaPausaCurtaTest() {
		
		Usuario usuario = DataHelper.createUsuario();
        when(usuarioRepository.salva(any())).thenReturn(usuario);
		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		usuarioApplicationService.mudaStatusParaPausaCurta(usuario.getEmail(), usuario.getIdUsuario());
		verify(usuarioRepository, times(1)).salva(any());
	}
 }