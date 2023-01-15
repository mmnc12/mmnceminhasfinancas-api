package com.mmnce.minhafinancasmmnce.service;

import java.util.Optional;

import com.mmnce.minhafinancasmmnce.model.entity.Usuario;

public interface UsuarioService {

	Usuario autenticar (String email, String Senha);
	
	Usuario salvarUsuario (Usuario usuario);
	
	void validarEmail (String email);
	
	Optional<Usuario> obterPorId( Long id );

}
