package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.domain.entity.administrativo.Responsavel;
import com.annaehugo.freepharma.domain.repository.administrativo.ResponsavelRepository;
import com.annaehugo.freepharma.domain.repository.administrativo.FarmaciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ResponsavelService {

    private final ResponsavelRepository responsavelRepository;
    private final FarmaciaRepository farmaciaRepository;

    @Autowired
    public ResponsavelService(ResponsavelRepository responsavelRepository, FarmaciaRepository farmaciaRepository) {
        this.responsavelRepository = responsavelRepository;
        this.farmaciaRepository = farmaciaRepository;
    }

    public List<Responsavel> listarTodos() {
        return responsavelRepository.findAll();
    }


    public List<Responsavel> listarPorFarmacia(Long farmaciaId) {
        return responsavelRepository.findByFarmaciaId(farmaciaId);
    }

    public List<Responsavel> listarAtivos() {
        return responsavelRepository.findByAtivoTrue();
    }

    public Optional<Responsavel> buscarPorId(Long id) {
        return responsavelRepository.findById(id);
    }

    public Optional<Responsavel> buscarPorCpf(String cpf) {
        return responsavelRepository.findByCpfCnpj(cpf);
    }

    public Optional<Responsavel> buscarPorEmail(String email) {
        return responsavelRepository.findByEmail(email);
    }

    public Responsavel salvar(Responsavel responsavel) {
        validarDadosResponsavel(responsavel);
        
        // Verificar se CPF já existe
        if (responsavel.getId() == null) {
            Optional<Responsavel> existenteCpf = buscarPorCpf(responsavel.getCpfCnpj());
            if (existenteCpf.isPresent()) {
                throw new RuntimeException("CPF já cadastrado para outro responsável");
            }
        }
        
        // Verificar se email já existe (se informado)
        if (responsavel.getEmail() != null && !responsavel.getEmail().trim().isEmpty()) {
            Optional<Responsavel> existenteEmail = buscarPorEmail(responsavel.getEmail());
            if (existenteEmail.isPresent() && !existenteEmail.get().getId().equals(responsavel.getId())) {
                throw new RuntimeException("Email já cadastrado");
            }
        }

        return responsavelRepository.save(responsavel);
    }

    public Responsavel atualizar(Long id, Responsavel responsavelAtualizado) {
        return responsavelRepository.findById(id)
                .map(responsavel -> {
                    responsavelAtualizado.setId(id);
                    
                    validarDadosResponsavel(responsavelAtualizado);
                    return responsavelRepository.save(responsavelAtualizado);
                })
                .orElseThrow(() -> new RuntimeException("Responsável não encontrado"));
    }

    public void deletar(Long id) {
        if (!responsavelRepository.existsById(id)) {
            throw new RuntimeException("Responsável não encontrado");
        }
        responsavelRepository.deleteById(id);
    }

    public void ativar(Long id) {
        responsavelRepository.findById(id)
                .map(responsavel -> {
                    responsavel.setAtivo(true);
                    return responsavelRepository.save(responsavel);
                })
                .orElseThrow(() -> new RuntimeException("Responsável não encontrado"));
    }

    public void inativar(Long id) {
        responsavelRepository.findById(id)
                .map(responsavel -> {
                    responsavel.setAtivo(false);
                    return responsavelRepository.save(responsavel);
                })
                .orElseThrow(() -> new RuntimeException("Responsável não encontrado"));
    }

    private void validarDadosResponsavel(Responsavel responsavel) {
        if (responsavel.getNome() == null || responsavel.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome é obrigatório");
        }
        
        if (responsavel.getCpfCnpj() == null || responsavel.getCpfCnpj().trim().isEmpty()) {
            throw new RuntimeException("CPF é obrigatório");
        }
        
        if (!isValidCpf(responsavel.getCpfCnpj())) {
            throw new RuntimeException("CPF inválido");
        }
        
        if (responsavel.getEmail() != null && !responsavel.getEmail().trim().isEmpty()) {
            if (!isValidEmail(responsavel.getEmail())) {
                throw new RuntimeException("Email inválido");
            }
        }
        
        // Validação de farmácia removida - não existe no modelo atual
        
        if (responsavel.getTelefone() != null && !responsavel.getTelefone().trim().isEmpty()) {
            if (responsavel.getTelefone().replaceAll("[^0-9]", "").length() < 10) {
                throw new RuntimeException("Telefone deve ter pelo menos 10 dígitos");
            }
        }
    }
    
    private boolean isValidCpf(String cpf) {
        return cpf != null && cpf.replaceAll("[^0-9]", "").length() == 11;
    }
    
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

}