package com.mmnce.minhafinancasmmnce.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.mmnce.minhafinancasmmnce.exception.ErroAutenticacao;
import com.mmnce.minhafinancasmmnce.exception.RegraNegocioException;
import com.mmnce.minhafinancasmmnce.model.entity.Usuario;
import com.mmnce.minhafinancasmmnce.model.repository.UsuarioRepository;
import com.mmnce.minhafinancasmmnce.service.impl.UsuarioServiceImpl;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	private UsuarioServiceImpl service;

	@MockBean
	UsuarioRepository repository;

	@Before
	public void setUp() {
		// repository = Mockito.mock(UsuarioRepository.class);
		// service = new UsuarioServiceImpl(repository);
	}

	@Test
	public void deveValidarEmail() {
		// Cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

		// Açao
		service.validarEmail("email@email.com");
	}

	@Test
	public void deveSalvarUmUsuario() {
		// Cenário
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder().id(1L).nome("nome").email("email@email.com").senha("senha").build();
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

		// Ação
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

		// Verificação
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
	}

	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		// Cenário
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

		// Ação
		org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.salvarUsuario(usuario);
		});

		// Verificação
		Mockito.verify(repository, Mockito.never()).save(usuario);

	}

	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		// Cenário
		String email = "email@email.com";
		String senha = "senha";

		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1L).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

		// Ação
		Usuario result = service.autenticar(email, senha);

		// Verificação
		Assertions.assertThat(result).isNotNull();
	}

	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComEmailInformado() {
		// Cenário
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

		// Ação
		// org.junit.jupiter.api.Assertions.assertThrows(ErroAutenticacao.class, () -> {
		// service.autenticar("email@email.com", "senha");
		// });
		Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "senha"));

		// Verificação
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Email e ou senha inválida.");
	}

	@Test
	public void deveLancarUmErroQuandoASenhaNaoBater() {
		// Cenário
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

		// Ação
		// org.junit.jupiter.api.Assertions.assertThrows(ErroAutenticacao.class, () -> {
		// service.autenticar("email@email.com", "123");
		// });
		Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "123"));
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Email e ou senha inválida.");
	}

	@Test
	public void deveLancarUmErroAoValidarEmailQuandoExistirEmailCadastrado() {
		// Cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

		// Ação
		org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validarEmail("email@email.com");
		});
	}
}
