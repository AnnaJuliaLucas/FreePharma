package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.administrativo.Unidade;
import com.annaehugo.freepharma.domain.repository.administrativo.UnidadeRepository;
import com.annaehugo.freepharma.domain.repository.administrativo.FarmaciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;
    private final FarmaciaRepository farmaciaRepository;

    @Autowired
    public UnidadeService(UnidadeRepository unidadeRepository, FarmaciaRepository farmaciaRepository) {
        this.unidadeRepository = unidadeRepository;
        this.farmaciaRepository = farmaciaRepository;
    }

    public List<Unidade> listarTodas() {
        return unidadeRepository.findAll();
    }

    public List<Unidade> listarPorFarmacia(Long farmaciaId) {
        return unidadeRepository.findByFarmaciaId(farmaciaId);
    }

    public Optional<Unidade> buscarPorId(Long id) {
        return unidadeRepository.findById(id);
    }

    public Optional<Unidade> buscarPorCnpj(String cnpj) {
        return unidadeRepository.findByCnpj(cnpj);
    }

    public List<Unidade> buscarPorStatus(String status) {
        return unidadeRepository.findByStatus(status);
    }

    public Unidade salvar(Unidade unidade) {
        validarDadosUnidade(unidade);
        
        if (unidade.getFarmacia() != null && unidade.getFarmacia().getId() != null) {
            if (!farmaciaRepository.existsById(unidade.getFarmacia().getId())) {
                throw new RuntimeException("Farmácia não encontrada");
            }
        }
        
        if (unidade.getId() == null && unidade.getCnpj() != null) {
            Optional<Unidade> existente = unidadeRepository.findByCnpj(unidade.getCnpj());
            if (existente.isPresent()) {
                throw new RuntimeException("CNPJ já cadastrado para outra unidade");
            }
        }
        return unidadeRepository.save(unidade);
    }

    public Unidade atualizar(Long id, Unidade unidadeAtualizada) {
        return unidadeRepository.findById(id)
                .map(unidade -> {
                    unidadeAtualizada.setId(id);
                    // Não permitir alterar farmácia e CNPJ
                    unidadeAtualizada.setFarmacia(unidade.getFarmacia());
                    if (unidade.getCnpj() != null) {
                        unidadeAtualizada.setCnpj(unidade.getCnpj());
                    }
                    validarDadosUnidade(unidadeAtualizada);
                    return unidadeRepository.save(unidadeAtualizada);
                })
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada"));
    }

    public void deletar(Long id) {
        if (!unidadeRepository.existsById(id)) {
            throw new RuntimeException("Unidade não encontrada");
        }
        
        // TODO: Implementar verificações de integridade referencial
        
        unidadeRepository.deleteById(id);
    }

    public void ativar(Long id) {
        unidadeRepository.findById(id)
                .map(unidade -> {
                    unidade.setStatus("ATIVA");
                    return unidadeRepository.save(unidade);
                })
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada"));
    }

    public void inativar(Long id) {
        unidadeRepository.findById(id)
                .map(unidade -> {
                    unidade.setStatus("INATIVA");
                    return unidadeRepository.save(unidade);
                })
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada"));
    }

    private void validarDadosUnidade(Unidade unidade) {
        if (unidade.getNomeFantasia() == null || unidade.getNomeFantasia().trim().isEmpty()) {
            throw new RuntimeException("Nome da unidade é obrigatório");
        }
        
        if (unidade.getEndereco() == null || unidade.getEndereco().trim().isEmpty()) {
            throw new RuntimeException("Endereço é obrigatório");
        }
        
        if (unidade.getFarmacia() == null || unidade.getFarmacia().getId() == null) {
            throw new RuntimeException("Farmácia é obrigatória");
        }
        
        if (unidade.getCnpj() != null && !unidade.getCnpj().trim().isEmpty()) {
            if (!isValidCnpj(unidade.getCnpj())) {
                throw new RuntimeException("CNPJ inválido");
            }
        }
        
        if (unidade.getEmail() != null && !unidade.getEmail().trim().isEmpty()) {
            if (!isValidEmail(unidade.getEmail())) {
                throw new RuntimeException("Email inválido");
            }
        }
    }

    private boolean isValidCnpj(String cnpj) {
        return cnpj != null && cnpj.replaceAll("[^0-9]", "").length() == 14;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}