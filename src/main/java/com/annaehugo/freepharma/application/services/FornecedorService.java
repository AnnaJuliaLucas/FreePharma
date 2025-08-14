package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.estoque.Fornecedor;
import com.annaehugo.freepharma.domain.repository.estoque.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    @Autowired
    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    public List<Fornecedor> listarTodos() {
        return fornecedorRepository.findAll();
    }

    public List<Fornecedor> listarAtivos() {
        return fornecedorRepository.findByStatus("ATIVO");
    }

    public List<Fornecedor> buscarPorNome(String razaoSocial) {
        return fornecedorRepository.findByRazaoSocial(razaoSocial);
    }

    public Optional<Fornecedor> buscarPorId(Long id) {
        return fornecedorRepository.findById(id);
    }

    public Optional<Fornecedor> buscarPorCnpj(String cnpj) {
        return fornecedorRepository.findByCnpj(cnpj);
    }

    public List<Fornecedor> buscarPorStatus(String status) {
        return fornecedorRepository.findByStatus(status);
    }

    public Fornecedor salvar(Fornecedor fornecedor) {
        validarDadosFornecedor(fornecedor);
        
        if (fornecedor.getId() == null) {
            Optional<Fornecedor> existente = fornecedorRepository.findByCnpj(fornecedor.getCnpj());
            if (existente.isPresent()) {
                throw new RuntimeException("CNPJ já cadastrado para outro fornecedor");
            }
        }
        
        if (fornecedor.getStatus() == null) {
            fornecedor.setStatus("ATIVO");
        }
        
        return fornecedorRepository.save(fornecedor);
    }

    public Fornecedor atualizar(Long id, Fornecedor fornecedorAtualizado) {
        return fornecedorRepository.findById(id)
                .map(fornecedor -> {
                    fornecedorAtualizado.setId(id);
                    // Não permitir alterar CNPJ
                    fornecedorAtualizado.setCnpj(fornecedor.getCnpj());
                    validarDadosFornecedor(fornecedorAtualizado);
                    return fornecedorRepository.save(fornecedorAtualizado);
                })
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
    }

    public void deletar(Long id) {
        if (!fornecedorRepository.existsById(id)) {
            throw new RuntimeException("Fornecedor não encontrado");
        }
        
        // Verificar se fornecedor tem produtos associados
        // TODO: Implementar verificação de integridade referencial
        
        fornecedorRepository.deleteById(id);
    }

    public void ativar(Long id) {
        fornecedorRepository.findById(id)
                .map(fornecedor -> {
                    fornecedor.setStatus("ATIVO");
                    return fornecedorRepository.save(fornecedor);
                })
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
    }

    public void inativar(Long id) {
        fornecedorRepository.findById(id)
                .map(fornecedor -> {
                    fornecedor.setStatus("INATIVO");
                    return fornecedorRepository.save(fornecedor);
                })
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
    }

    public void bloquear(Long id) {
        fornecedorRepository.findById(id)
                .map(fornecedor -> {
                    fornecedor.setStatus("BLOQUEADO");
                    return fornecedorRepository.save(fornecedor);
                })
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
    }

    private void validarDadosFornecedor(Fornecedor fornecedor) {
        if (fornecedor.getNomeFantasia() == null || fornecedor.getNomeFantasia().trim().isEmpty()) {
            throw new RuntimeException("Nome do fornecedor é obrigatório");
        }
        
        if (fornecedor.getCnpj() == null || fornecedor.getCnpj().trim().isEmpty()) {
            throw new RuntimeException("CNPJ é obrigatório");
        }
        
        if (!isValidCnpj(fornecedor.getCnpj())) {
            throw new RuntimeException("CNPJ inválido");
        }
        
        if (fornecedor.getEmail() != null && !fornecedor.getEmail().trim().isEmpty()) {
            if (!isValidEmail(fornecedor.getEmail())) {
                throw new RuntimeException("Email inválido");
            }
        }
        
        if (fornecedor.getEndereco() == null || fornecedor.getEndereco().trim().isEmpty()) {
            throw new RuntimeException("Endereço é obrigatório");
        }
    }

    private boolean isValidCnpj(String cnpj) {
        return cnpj != null && cnpj.replaceAll("[^0-9]", "").length() == 14;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}