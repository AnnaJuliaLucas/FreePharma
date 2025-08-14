// Mock data generators for Cypress tests
class MockDataGenerator {
  
  // Gerar CNPJ válido (formato com pontuação: XX.XXX.XXX/0001-XX)
  static generateCNPJ() {
    const base = Math.floor(Math.random() * 100000000).toString().padStart(8, '1');
    const filial = '0001';
    const verificadores = Math.floor(Math.random() * 100).toString().padStart(2, '0');
    const cnpjNumeros = base + filial + verificadores;
    
    // Aplicar formatação XX.XXX.XXX/0001-XX
    return cnpjNumeros.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, '$1.$2.$3/$4-$5');
  }
  
  // Gerar CPF válido (formato com pontuação: XXX.XXX.XXX-XX)
  static generateCPF() {
    const base = Math.floor(Math.random() * 1000000000).toString().padStart(9, '1');
    const verificadores = Math.floor(Math.random() * 100).toString().padStart(2, '0');
    const cpfNumeros = base + verificadores;
    
    // Aplicar formatação XXX.XXX.XXX-XX
    return cpfNumeros.replace(/^(\d{3})(\d{3})(\d{3})(\d{2})$/, '$1.$2.$3-$4');
  }
  
  // Gerar email único
  static generateEmail(prefix = 'teste') {
    const random = Math.floor(Math.random() * 1000000);
    return `${prefix}${random}@cypress.com`;
  }
  
  // Gerar nome de pessoa
  static generatePersonName() {
    const nomes = ['João', 'Maria', 'Pedro', 'Ana', 'Carlos', 'Fernanda', 'Ricardo', 'Juliana'];
    const sobrenomes = ['Silva', 'Santos', 'Oliveira', 'Souza', 'Rodrigues', 'Ferreira', 'Alves', 'Pereira'];
    const nome = nomes[Math.floor(Math.random() * nomes.length)];
    const sobrenome = sobrenomes[Math.floor(Math.random() * sobrenomes.length)];
    return `Dr. ${nome} ${sobrenome}`;
  }
  
  // Gerar nome de empresa
  static generateCompanyName() {
    const tipos = ['Farmácia', 'Drogaria', 'Farmácias', 'Medicamentos'];
    const nomes = ['Saúde', 'Vida', 'Popular', 'Central', 'Nova', 'Moderna', 'Express', 'Plus'];
    const sufixos = ['Ltda', 'S.A.', 'ME', 'EIRELI'];
    
    const tipo = tipos[Math.floor(Math.random() * tipos.length)];
    const nome = nomes[Math.floor(Math.random() * nomes.length)];
    const sufixo = sufixos[Math.floor(Math.random() * sufixos.length)];
    
    return `${tipo} ${nome} ${sufixo}`;
  }
  
  // Gerar endereço
  static generateAddress() {
    const tipos = ['Rua', 'Avenida', 'Alameda', 'Praça'];
    const nomes = ['das Flores', 'Central', 'Paulista', 'do Comércio', 'da Paz', 'Brasil', 'Santos Dumont', 'JK'];
    const numero = Math.floor(Math.random() * 9999) + 1;
    
    const tipo = tipos[Math.floor(Math.random() * tipos.length)];
    const nome = nomes[Math.floor(Math.random() * nomes.length)];
    
    return `${tipo} ${nome}, ${numero}`;
  }
  
  // Gerar telefone
  static generatePhone() {
    const ddd = Math.floor(Math.random() * 89) + 11; // DDDs de 11 a 99
    const numero = Math.floor(Math.random() * 900000000) + 100000000; // 9 dígitos
    return `${ddd}${numero}`;
  }
  
  // Gerar dados completos de farmácia
  static generateFarmaciaData() {
    const razaoSocial = this.generateCompanyName();
    const nomeFantasia = razaoSocial.split(' ').slice(0, 2).join(' '); // Pega as duas primeiras palavras
    
    return {
      razaoSocial: razaoSocial,
      nomeFantasia: nomeFantasia,
      cnpj: this.generateCNPJ(),
      inscricaoEstadual: Math.floor(Math.random() * 1000000000).toString(),
      endereco: this.generateAddress(),
      telefoneContato: this.generatePhone(),
      emailContato: this.generateEmail('farmacia'),
      status: 'ATIVA'
    };
  }
  
  // Gerar dados completos de responsável
  static generateResponsavelData(farmaciaId) {
    return {
      nome: this.generatePersonName(),
      cpfCnpj: this.generateCPF(),
      email: this.generateEmail('responsavel'),
      telefone: this.generatePhone(),
      registroProfissional: `CRF-SP ${Math.floor(Math.random() * 99999)}`,
      farmaciaId: farmaciaId,
      ativo: true
    };
  }
  
  // Gerar dados completos de usuário
  static generateUsuarioData(unidadeId) {
    const nome = this.generatePersonName().replace('Dr. ', '');
    const login = nome.toLowerCase().replace(' ', '.') + Math.floor(Math.random() * 1000);
    
    return {
      login: login,
      nome: nome,
      email: this.generateEmail('usuario'),
      senha: 'senha123456',
      telefone: this.generatePhone(),
      cargo: 'Administrador',
      status: 'ATIVO',
      unidadesAcesso: unidadeId ? [{ id: unidadeId }] : []
    };
  }
  
  // Gerar dados de fornecedor
  static generateFornecedorData() {
    const razaoSocial = this.generateCompanyName();
    const nomeFantasia = razaoSocial.split(' ').slice(0, 2).join(' ');
    
    return {
      nomeFantasia: nomeFantasia,
      razaoSocial: razaoSocial,
      cnpj: this.generateCNPJ(),
      inscricaoEstadual: Math.floor(Math.random() * 1000000000).toString(),
      endereco: this.generateAddress(),
      telefone: this.generatePhone(),
      email: this.generateEmail('fornecedor'),
      contato: this.generatePersonName().replace('Dr. ', ''),
      status: 'ATIVO'
    };
  }
}

// Disponibilizar globalmente
window.MockDataGenerator = MockDataGenerator;

// Exportar para uso nos testes
export default MockDataGenerator;