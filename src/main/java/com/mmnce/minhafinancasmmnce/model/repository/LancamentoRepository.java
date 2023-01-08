package com.mmnce.minhafinancasmmnce.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mmnce.minhafinancasmmnce.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
