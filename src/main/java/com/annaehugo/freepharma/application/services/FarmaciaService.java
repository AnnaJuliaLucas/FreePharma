package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.administrativo.Farmacia;
import com.annaehugo.freepharma.domain.repository.administrativo.FarmaciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FarmaciaService {

    private final FarmaciaRepository farmaciaRepository;

    @Autowired
    public FarmaciaService(FarmaciaRepository farmaciaRepository) {
        this.farmaciaRepository = farmaciaRepository;
    }

    public List<Farmacia> listarTodas() {
        return farmaciaRepository.findAll();
    }

    public Optional<Farmacia> buscarPorId(Long id) {
        return farmaciaRepository.findById(id);
    }

    public Optional<Farmacia> buscarPorCnpj(String cnpj) {
        return farmaciaRepository.findByCnpj(cnpj);
    }

    public List<Farmacia> buscarPorStatus(String status) {
        return farmaciaRepository.findByStatus(status);
    }

    public Farmacia salvar(Farmacia farmacia) {
        validarDadosFarmacia(farmacia);
        
        if (farmacia.getId() == null) {
            Optional<Farmacia> existente = farmaciaRepository.findByCnpj(farmacia.getCnpj());
            if (existente.isPresent()) {
                throw new RuntimeException("CNPJ já cadastrado para outra farmácia");
            }
        }
        return farmaciaRepository.save(farmacia);
    }

    public Farmacia atualizar(Long id, Farmacia farmaciaAtualizada) {
        return farmaciaRepository.findById(id)
                .map(farmacia -> {
                    // Não permitir alterar CNPJ
                    farmaciaAtualizada.setId(id);
                    farmaciaAtualizada.setCnpj(farmacia.getCnpj());
                    validarDadosFarmacia(farmaciaAtualizada);
                    return farmaciaRepository.save(farmaciaAtualizada);
                })
                .orElseThrow(() -> new RuntimeException("Farmácia não encontrada"));
    }

    public void deletar(Long id) {
        if (!farmaciaRepository.existsById(id)) {
            throw new RuntimeException("Farmácia não encontrada");
        }
        farmaciaRepository.deleteById(id);
    }

    public void ativar(Long id) {
        farmaciaRepository.findById(id)
                .map(farmacia -> {
                    farmacia.setStatus("ATIVA");
                    return farmaciaRepository.save(farmacia);
                })
                .orElseThrow(() -> new RuntimeException("Farmácia não encontrada"));
    }

    public void inativar(Long id) {
        farmaciaRepository.findById(id)
                .map(farmacia -> {
                    farmacia.setStatus("INATIVA");
                    return farmaciaRepository.save(farmacia);
                })
                .orElseThrow(() -> new RuntimeException("Farmácia não encontrada"));
    }

    private void validarDadosFarmacia(Farmacia farmacia) {
        if (farmacia.getRazaoSocial() == null || farmacia.getRazaoSocial().trim().isEmpty()) {
            throw new RuntimeException("Razão social é obrigatória");
        }
        
        if (farmacia.getCnpj() == null || farmacia.getCnpj().trim().isEmpty()) {
            throw new RuntimeException("CNPJ é obrigatório");
        }
        
        if (!isValidCnpj(farmacia.getCnpj())) {
            throw new RuntimeException("CNPJ inválido");
        }
        
        if (farmacia.getEmailContato() != null && !isValidEmail(farmacia.getEmailContato())) {
            throw new RuntimeException("Email inválido");
        }
    }

    private boolean isValidCnpj(String cnpj) {
        return cnpj != null && cnpj.replaceAll("[^0-9]", "").length() == 14;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}