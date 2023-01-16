package com.mmnce.minhafinancasmmnce.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
//import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.mmnce.minhafinancasmmnce.exception.RegraNegocioException;
import com.mmnce.minhafinancasmmnce.model.entity.Lancamento;
import com.mmnce.minhafinancasmmnce.model.entity.Usuario;
import com.mmnce.minhafinancasmmnce.model.enums.StatusLancamento;
import com.mmnce.minhafinancasmmnce.model.repository.LancamentoRepository;
import com.mmnce.minhafinancasmmnce.model.repository.LancamentoRepositoryTest;
import com.mmnce.minhafinancasmmnce.service.impl.LancamentoServiceImpl;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	@MockBean
	LancamentoRepository repository;

	@Test
	public void deveSalvarUmLancamento() {

		// Cenário
		Lancamento lancamentoASalvo = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvo);

		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvo)).thenReturn(lancamentoSalvo);

		// execução
		Lancamento lancamento = service.salvar(lancamentoASalvo);

		// verificação
		assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);

	}

	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		// Cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

		// Execução e verificação
		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}

	@Test
	public void deveAtualizarUmLancamento() {

		// Cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

		Mockito.doNothing().when(service).validar(lancamentoSalvo);

		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

		// execução
		service.atualizar(lancamentoSalvo);

		// verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);

	}

	@Test
	public void deveLancarUmErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		// Execução e verificação
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamento);
	}

	@Test
	public void deveDeletarUmLancamento() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);

		// Execução
		service.deletar(lancamento);

		// Verificar
		Mockito.verify(repository).delete(lancamento);
	}

	public void deveLancarUmErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		// Execução
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);

		// Verificar
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}

	@Test
	public void deveFiltrarLancamentos() {
		// Centário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);

		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

		// Execução
		List<Lancamento> resultado = service.buscar(lancamento);

		// Verificação
		Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}

	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		lancamento.setStatus(StatusLancamento.PENDENTE);

		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

		// Execução
		service.atualizarStatus(lancamento, novoStatus);

		// Verificação
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	}

	@Test
	public void deveObterUmLancamentoPorId() {
		// Cenário
		Long id = 1L;

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

		// Execução
		Optional<Lancamento> resultado = service.obterPorId(id);

		// Verificação
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}

	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		// Cenário
		Long id = 1L;

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		// Execução
		Optional<Lancamento> resultado = service.obterPorId(id);

		// Verificação
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}

	@Test
	public void deveLancarErroAoValidarUmLancamento() {
		
		Lancamento lancamento = new Lancamento();

		Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Informe uma descrição válida.");

		lancamento.setDescricao("");

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Informe uma descrição válida.");

		lancamento.setDescricao("salario");

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

		lancamento.setMes(0);

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

		lancamento.setMes(13);

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

		lancamento.setMes(1);

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");

		lancamento.setAno(202);

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");

		lancamento.setAno(2020);

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário.");

		lancamento.setUsuario(new Usuario());

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário.");

		lancamento.getUsuario().setId(1L);

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");

		lancamento.setValor(BigDecimal.ZERO);

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");

		lancamento.setValor(BigDecimal.valueOf(1));

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Informe um tipo de lançamento.");

	}

}
