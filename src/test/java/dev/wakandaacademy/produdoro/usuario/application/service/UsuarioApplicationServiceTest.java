package dev.wakandaacademy.produdoro.usuario.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;

import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

@ExtendWith(MockitoExtension.class)
class UsuarioApplicationServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @InjectMocks
    private UsuarioApplicationService usuarioApplicationService;

    @Test
    public void MudaStatusParaPausaLongaTest() {

        Usuario usuario = DataHelper.createUsuario();
        when(usuarioRepository.salva(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        usuarioApplicationService.mudaStatusParaPausaLonga(usuario.getEmail(), usuario.getIdUsuario());
        verify(usuarioRepository, times(1)).salva(any());
        assertEquals(StatusUsuario.PAUSA_LONGA, usuario.getStatus());
    }
    
    @Test
    public void naoDeveMudaStatusParaPausaLongaTest() {
        UUID idUsuario2 = UUID.randomUUID();
        Usuario usuario = DataHelper.createUsuario();
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        APIException ex = assertThrows(APIException.class, () -> {usuarioApplicationService.mudaStatusParaPausaLonga(usuario.getEmail(), idUsuario2);});
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusException());
        assertEquals("Credencial de autenticação não é válida", ex.getMessage());
    }
	
	@Test
	void deveAlterarStatusParaFoco() {
		Usuario usuario = DataHelper.createUsuario();
		when(usuarioRepository.salva(any())).thenReturn(usuario);
		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		usuarioApplicationService.atualizaStatusParaFoco(usuario.getEmail(), usuario.getIdUsuario());
		verify(usuarioRepository, times(1)).salva(any());

	}

	@Test
	void statusParaFocoFalha() {
		Usuario usuario = DataHelper.createUsuario();
		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		APIException exception = assertThrows(APIException.class,
				() -> usuarioApplicationService.atualizaStatusParaFoco("borgestatielle@gmail,com", UUID.randomUUID()));
		assertEquals("Credencial de autenticação não é válida", exception.getMessage());
		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusException());

	}
}