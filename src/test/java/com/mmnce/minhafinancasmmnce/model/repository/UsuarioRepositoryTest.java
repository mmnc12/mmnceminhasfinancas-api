package com.mmnce.minhafinancasmmnce.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.mmnce.minhafinancasmmnce.model.entity.Usuario;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository repository;

	@Autowired
	TestEntityManager entityManager;

	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		// Cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		// Ação / Execução
		Boolean result = repository.existsByEmail("usuario@email.com");

		// Verificação
		Assertions.assertThat(result).isTrue();
	}

	@Test
	public void deveVoltarFalsoQuandoNãoHouverUsuarioCadastroComOEmail() {
		// Cenário

		// Açao
		Boolean result = repository.existsByEmail("usuario@email.com");

		// Verificação
		Assertions.assertThat(result).isFalse();

	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		// Cenário
		Usuario usuario = criarUsuario();
		
		// Ação
		Usuario usuarioSalvo = entityManager.persist(usuario);
		
		// Verificação
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		// Cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		// Ação
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		// Verificação
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioAoBuscarUmUsuarioPorEmailQuantoNaoExistirNaBaseDeDados() {
		// Cenário
		
		// Ação
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		// Verificação
		Assertions.assertThat(result.isPresent()).isFalse();
		
	}
	

	public static Usuario criarUsuario() {
		return Usuario.builder()
				.nome("usuario")
				.email("usuario@email.com")
				.senha("senha")
				.build();
	}
}
