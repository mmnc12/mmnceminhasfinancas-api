package com.mmnce.minhafinancasmmnce.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mmnce.minhafinancasmmnce.exception.ErroAutenticacao;
import com.mmnce.minhafinancasmmnce.exception.RegraNegocioException;
import com.mmnce.minhafinancasmmnce.model.entity.Usuario;
import com.mmnce.minhafinancasmmnce.model.repository.UsuarioRepository;
import com.mmnce.minhafinancasmmnce.service.UsuarioService;

import jakarta.transaction.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	private UsuarioRepository repository;

	public UsuarioServiceImpl(UsuarioRepository repository) {
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		if(!usuario.isPresent()) {
			//throw new ErroAutenticacao("Email e ou senha inválida.");
			throw new ErroAutenticacao("Usuario não encontrado para o email informado.");
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			//throw new ErroAutenticacao("Email e ou senha inválida.");
			throw new ErroAutenticacao("Senha inválida.");
		}
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {

		Boolean existe = repository.existsByEmail(email);
		if (existe) {
			throw new RegraNegocioException("Já existe um usuário cadastro com este email.");
		}
	}

}
