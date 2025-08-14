package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.estoque.ProdutoReferencia;
import com.annaehugo.freepharma.domain.entity.estoque.Medicamento;
import com.annaehugo.freepharma.domain.repository.estoque.ProdutoReferenciaRepository;
import com.annaehugo.freepharma.domain.repository.estoque.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProdutoReferenciaService {

    private final ProdutoReferenciaRepository produtoRepository;
    private final MedicamentoRepository medicamentoRepository;

    @Autowired
    public ProdutoReferenciaService(
            ProdutoReferenciaRepository produtoRepository,
            MedicamentoRepository medicamentoRepository) {
        this.produtoRepository = produtoRepository;
        this.medicamentoRepository = medicamentoRepository;
    }

    public List<ProdutoReferencia> listarTodos() {
        return produtoRepository.findAll();
    }

    public List<ProdutoReferencia> listarAtivos() {
        return produtoRepository.findByStatus("ATIVO");
    }

    public List<ProdutoReferencia> buscarPorNome(String nome) {
        return produtoRepository.findByNome(nome);
    }

    public Optional<ProdutoReferencia> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public Optional<ProdutoReferencia> buscarPorCodigoInterno(String codigoInterno) {
        return produtoRepository.findByCodigoInterno(codigoInterno);
    }

    public Optional<ProdutoReferencia> buscarPorEan(String ean) {
        return produtoRepository.findByEan(ean);
    }

    public List<ProdutoReferencia> buscarPorStatus(String status) {
        return produtoRepository.findByStatus(status);
    }

    public ProdutoReferencia salvar(ProdutoReferencia produto) {
        validarDadosProduto(produto);
        
        if (produto.getId() == null) {
            Optional<ProdutoReferencia> existente = produtoRepository.findByCodigoInterno(produto.getCodigoInterno());
            if (existente.isPresent()) {
                throw new RuntimeException("Código interno já existe");
            }
        }
        
        if (produto.getEan() != null && !produto.getEan().trim().isEmpty()) {
            Optional<ProdutoReferencia> existenteEan = produtoRepository.findByEan(produto.getEan());
            if (existenteEan.isPresent() && !existenteEan.get().getId().equals(produto.getId())) {
                throw new RuntimeException("EAN já cadastrado");
            }
        }
        
        if (produto.getStatus() == null) {
            produto.setStatus("ATIVO");
        }
        
        return produtoRepository.save(produto);
    }

    public ProdutoReferencia atualizar(Long id, ProdutoReferencia produtoAtualizado) {
        return produtoRepository.findById(id)
                .map(produto -> {
                    produtoAtualizado.setId(id);
                    // Não permitir alterar código interno
                    produtoAtualizado.setCodigoInterno(produto.getCodigoInterno());
                    validarDadosProduto(produtoAtualizado);
                    return produtoRepository.save(produtoAtualizado);
                })
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public void deletar(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado");
        }
        
        // TODO: Implementar verificação de integridade referencial
        produtoRepository.deleteById(id);
    }

    public void ativar(Long id) {
        produtoRepository.findById(id)
                .map(produto -> {
                    produto.setStatus("ATIVO");
                    return produtoRepository.save(produto);
                })
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public void inativar(Long id) {
        produtoRepository.findById(id)
                .map(produto -> {
                    produto.setStatus("INATIVO");
                    return produtoRepository.save(produto);
                })
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public void descontinuar(Long id) {
        produtoRepository.findById(id)
                .map(produto -> {
                    produto.setStatus("DESCONTINUADO");
                    return produtoRepository.save(produto);
                })
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public List<Medicamento> listarMedicamentos() {
        return medicamentoRepository.findAll();
    }

    public List<Medicamento> buscarMedicamentosPorPrincipioAtivo(String principioAtivo) {
        return medicamentoRepository.findByPrincipioAtivoContainingIgnoreCase(principioAtivo);
    }

    public List<Medicamento> listarMedicamentosControlados() {
        return medicamentoRepository.findByControladoTrue();
    }

    public List<Medicamento> listarMedicamentosGenericos() {
        return medicamentoRepository.findByGenericoTrue();
    }

    public Optional<Medicamento> buscarMedicamentoPorRegistroAnvisa(String registroAnvisa) {
        return medicamentoRepository.findByRegistroAnvisa(registroAnvisa);
    }

    private void validarDadosProduto(ProdutoReferencia produto) {
        if (produto.getCodigoInterno() == null || produto.getCodigoInterno().trim().isEmpty()) {
            throw new RuntimeException("Código interno é obrigatório");
        }
        
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome do produto é obrigatório");
        }
        
        if (produto.getUnidadeMedida() == null || produto.getUnidadeMedida().trim().isEmpty()) {
            throw new RuntimeException("Unidade de medida é obrigatória");
        }

        if (produto instanceof Medicamento) {
            Medicamento medicamento = (Medicamento) produto;
            
            if (medicamento.getPrincipioAtivo() == null || medicamento.getPrincipioAtivo().trim().isEmpty()) {
                throw new RuntimeException("Princípio ativo é obrigatório para medicamentos");
            }
            
            if (medicamento.getFormaFarmaceutica() == null || medicamento.getFormaFarmaceutica().trim().isEmpty()) {
                throw new RuntimeException("Forma farmacêutica é obrigatória para medicamentos");
            }
            
            if (medicamento.getLaboratorio() == null || medicamento.getLaboratorio().trim().isEmpty()) {
                throw new RuntimeException("Laboratório é obrigatório para medicamentos");
            }
        }
    }
}