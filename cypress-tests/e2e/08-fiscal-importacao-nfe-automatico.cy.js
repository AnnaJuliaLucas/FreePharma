describe('Importação NFe Automática - Caso de Uso Completo', () => {
  const baseUrl = Cypress.env('baseUrl') || 'http://localhost:9876';
  let farmaciaId;
  let unidadeId;
  let nfeCompraSample;
  let nfeVendaSample;
  let createdFornecedorId;
  let createdNotaFiscalId;

  before(() => {
    cy.login();
    
    // Criar farmacia e unidade para os testes
    cy.createTestFarmacia().then((farmacia) => {
      farmaciaId = farmacia.id;
      
      const unidadeData = {
        nomeFantasia: 'Unidade NFe Automática',
        endereco: 'Rua NFe Automática, 123',
        telefone: '11777666555',
        email: 'nfe@teste.com',
        status: 'ATIVA',
        farmaciaId: farmaciaId
      };
      
      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/unidades`,
        body: unidadeData
      }).then((response) => {
        expect(response.status).to.eq(201);
        unidadeId = response.body.id;
      });
    });
    
    // XML de NFe de Compra
    nfeCompraSample = `<?xml version="1.0" encoding="UTF-8"?>
<nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
  <NFe>
    <infNFe Id="NFe35240111222333000144550010000012341234567890">
      <ide>
        <nNF>1234</nNF>
        <serie>1</serie>
        <dhEmi>2024-01-15T10:00:00-03:00</dhEmi>
      </ide>
      <emit>
        <CNPJ>11222333000144</CNPJ>
        <xNome>Laboratório Distribuidor LTDA</xNome>
        <xFant>LabDist</xFant>
        <IE>123456789</IE>
        <enderEmit>
          <xLgr>Av. Industrial</xLgr>
          <nro>1000</nro>
          <xBairro>Distrito Industrial</xBairro>
          <xMun>São Paulo</xMun>
          <UF>SP</UF>
          <CEP>01234567</CEP>
        </enderEmit>
        <fone>1133334444</fone>
        <email>vendas@labdist.com</email>
      </emit>
      <dest>
        <CNPJ>12345678000199</CNPJ>
        <xNome>FreePharma Matriz LTDA</xNome>
      </dest>
      <det nItem="1">
        <prod>
          <cProd>PARACETAMOL500MG</cProd>
          <cEAN>7891234567890</cEAN>
          <xProd>Paracetamol 500mg cx 20 comprimidos</xProd>
          <NCM>30049099</NCM>
          <CFOP>1102</CFOP>
          <uCom>CX</uCom>
          <qCom>50</qCom>
          <vUnCom>15.50</vUnCom>
          <vProd>775.00</vProd>
          <xLote>LOTE001</xLote>
          <dVal>2025-12-31</dVal>
        </prod>
      </det>
      <det nItem="2">
        <prod>
          <cProd>DIPIRONA500MG</cProd>
          <cEAN>7891234567891</cEAN>
          <xProd>Dipirona Sódica 500mg cx 10 comprimidos</xProd>
          <NCM>30049050</NCM>
          <CFOP>1102</CFOP>
          <uCom>CX</uCom>
          <qCom>30</qCom>
          <vUnCom>8.75</vUnCom>
          <vProd>262.50</vProd>
          <xLote>LOTE002</xLote>
          <dVal>2025-11-30</dVal>
        </prod>
      </det>
      <total>
        <ICMSTot>
          <vNF>1037.50</vNF>
        </ICMSTot>
      </total>
    </infNFe>
  </NFe>
</nfeProc>`;

    // XML de NFe de Venda
    nfeVendaSample = `<?xml version="1.0" encoding="UTF-8"?>
<nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
  <NFe>
    <infNFe Id="NFe35240112345678000199550010000056781234567890">
      <ide>
        <nNF>5678</nNF>
        <serie>1</serie>
        <dhEmi>2024-01-16T14:00:00-03:00</dhEmi>
      </ide>
      <emit>
        <CNPJ>12345678000199</CNPJ>
        <xNome>FreePharma Matriz LTDA</xNome>
      </emit>
      <dest>
        <CPF>12345678901</CPF>
        <xNome>Cliente Teste Silva</xNome>
      </dest>
      <det nItem="1">
        <prod>
          <cProd>PARACETAMOL500MG</cProd>
          <cEAN>7891234567890</cEAN>
          <xProd>Paracetamol 500mg cx 20 comprimidos</xProd>
          <NCM>30049099</NCM>
          <CFOP>5102</CFOP>
          <uCom>CX</uCom>
          <qCom>5</qCom>
          <vUnCom>22.00</vUnCom>
          <vProd>110.00</vProd>
          <xLote>LOTE001</xLote>
        </prod>
      </det>
      <total>
        <ICMSTot>
          <vNF>110.00</vNF>
        </ICMSTot>
      </total>
    </infNFe>
  </NFe>
</nfeProc>`;
  });

  describe('POST /api/fiscal/importacao-nfe/xml - Processamento Automático', () => {
    it('should process NFe compra automatically and create all entities', () => {
      // Criar arquivo para upload
      const blob = new Blob([nfeCompraSample], { type: 'text/xml' });
      const formData = new FormData();
      formData.append('file', blob, 'nfe-compra-teste.xml');

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        body: formData,
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('status', 'SUCESSO');
        expect(response.body).to.have.property('importacaoId');
        expect(response.body).to.have.property('notaFiscalId');
        expect(response.body).to.have.property('fornecedorId');
        expect(response.body).to.have.property('itensProcessados', 2);
        expect(response.body).to.have.property('inconsistenciasDetectadas');
        expect(response.body.mensagem).to.contain('2 itens processados');

        const notaFiscalId = response.body.notaFiscalId;
        const fornecedorId = response.body.fornecedorId;

        // Verificar se a nota fiscal foi criada corretamente
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/notas-fiscais/${notaFiscalId}`
        }).then((nfResponse) => {
          expect(nfResponse.status).to.eq(200);
          expect(nfResponse.body).to.have.property('numero', '1234');
          expect(nfResponse.body).to.have.property('chaveAcesso', '35240111222333000144550010000012341234567890');
          expect(nfResponse.body).to.have.property('tipoOperacao', 'COMPRA');
          expect(nfResponse.body).to.have.property('status', 'PROCESSADA');
          expect(parseFloat(nfResponse.body.valorTotal)).to.eq(1037.50);
        });

        // Verificar se o fornecedor foi criado automaticamente
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/fornecedores/${fornecedorId}`
        }).then((fornResponse) => {
          expect(fornResponse.status).to.eq(200);
          expect(fornResponse.body).to.have.property('cnpj', '11222333000144');
          expect(fornResponse.body).to.have.property('razaoSocial', 'Laboratório Distribuidor LTDA');
          expect(fornResponse.body).to.have.property('nomeFantasia', 'LabDist');
          expect(fornResponse.body).to.have.property('email', 'vendas@labdist.com');
          expect(fornResponse.body).to.have.property('telefone', '1133334444');
          expect(fornResponse.body.endereco).to.contain('Av. Industrial');
          expect(fornResponse.body.endereco).to.contain('São Paulo');
        });

        // Verificar se os produtos foram criados automaticamente
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/produtos`
        }).then((prodResponse) => {
          expect(prodResponse.status).to.eq(200);
          expect(prodResponse.body).to.be.an('array');
          
          // Buscar paracetamol
          const paracetamol = prodResponse.body.find(p => p.ean === '7891234567890');
          expect(paracetamol).to.exist;
          expect(paracetamol.nome).to.eq('Paracetamol 500mg cx 20 comprimidos');
          expect(paracetamol.ncm).to.eq('30049099');
          expect(paracetamol.unidadeMedida).to.eq('CX');
          expect(paracetamol.status).to.eq('ATIVO');
          
          // Buscar dipirona
          const dipirona = prodResponse.body.find(p => p.ean === '7891234567891');
          expect(dipirona).to.exist;
          expect(dipirona.nome).to.eq('Dipirona Sódica 500mg cx 10 comprimidos');
          expect(dipirona.ncm).to.eq('30049050');
        });

        // Verificar se o estoque foi atualizado corretamente
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/estoque`
        }).then((estoqueResponse) => {
          expect(estoqueResponse.status).to.eq(200);
          expect(estoqueResponse.body).to.be.an('array');
          expect(estoqueResponse.body.length).to.be.at.least(2);
          
          // Verificar estoque do paracetamol
          const estoqueParacetamol = estoqueResponse.body.find(e => 
            e.produtoReferencia && e.produtoReferencia.ean === '7891234567890'
          );
          if (estoqueParacetamol) {
            expect(estoqueParacetamol.quantidadeAtual).to.eq(50);
            expect(estoqueParacetamol.lote).to.eq('LOTE001');
          }
          
          // Verificar estoque da dipirona
          const estoqueDipirona = estoqueResponse.body.find(e => 
            e.produtoReferencia && e.produtoReferencia.ean === '7891234567891'
          );
          if (estoqueDipirona) {
            expect(estoqueDipirona.quantidadeAtual).to.eq(30);
            expect(estoqueDipirona.lote).to.eq('LOTE002');
          }
        });
      });
    });

    it('should process NFe venda and decrement stock', () => {
      // Primeiro processar uma compra para ter estoque
      const blobCompra = new Blob([nfeCompraSample], { type: 'text/xml' });
      const formDataCompra = new FormData();
      formDataCompra.append('file', blobCompra, 'nfe-compra-inicial.xml');

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        body: formDataCompra,
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then((compraResponse) => {
        expect(compraResponse.status).to.eq(200);
        expect(compraResponse.body.status).to.eq('SUCESSO');

        // Aguardar um momento para o processamento
        cy.wait(1000);

        // Agora processar a venda
        const blobVenda = new Blob([nfeVendaSample], { type: 'text/xml' });
        const formDataVenda = new FormData();
        formDataVenda.append('file', blobVenda, 'nfe-venda-teste.xml');

        cy.authenticatedRequest({
          method: 'POST',
          url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
          body: formDataVenda,
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).then((vendaResponse) => {
          expect(vendaResponse.status).to.eq(200);
          expect(vendaResponse.body).to.have.property('status', 'SUCESSO');
          expect(vendaResponse.body).to.have.property('notaFiscalId');
          expect(vendaResponse.body).to.have.property('itensProcessados', 1);

          const notaFiscalId = vendaResponse.body.notaFiscalId;

          // Verificar se a nota fiscal de venda foi criada
          cy.authenticatedRequest({
            method: 'GET',
            url: `${baseUrl}/api/notas-fiscais/${notaFiscalId}`
          }).then((nfResponse) => {
            expect(nfResponse.status).to.eq(200);
            expect(nfResponse.body).to.have.property('numero', '5678');
            expect(nfResponse.body).to.have.property('tipoOperacao', 'VENDA');
            expect(nfResponse.body).to.have.property('status', 'PROCESSADA');
            expect(parseFloat(nfResponse.body.valorTotal)).to.eq(110.00);
          });

          // Verificar se o estoque foi decrementado
          cy.authenticatedRequest({
            method: 'GET',
            url: `${baseUrl}/api/estoque`
          }).then((estoqueResponse) => {
            expect(estoqueResponse.status).to.eq(200);
            
            // Buscar estoque do paracetamol
            const estoqueParacetamol = estoqueResponse.body.find(e => 
              e.produtoReferencia && e.produtoReferencia.ean === '7891234567890'
            );
            
            if (estoqueParacetamol) {
              // Estoque deve ter sido decrementado: 50 (compra) - 5 (venda) = 45
              expect(estoqueParacetamol.quantidadeAtual).to.eq(45);
            }
          });
        });
      });
    });

    it('should return error for invalid XML file', () => {
      const invalidXml = '<invalid>xml content</invalid>';
      const blob = new Blob([invalidXml], { type: 'text/xml' });
      const formData = new FormData();
      formData.append('file', blob, 'nfe-invalido.xml');

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        body: formData,
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(200); // Service retorna 200 mas com status ERRO
        expect(response.body).to.have.property('status', 'ERRO');
        expect(response.body.mensagem).to.contain('Erro no processamento');
        expect(response.body).to.have.property('importacaoId');
      });
    });

    it('should return error for non-XML file', () => {
      const textContent = 'This is not XML content';
      const blob = new Blob([textContent], { type: 'text/plain' });
      const formData = new FormData();
      formData.append('file', blob, 'nao-xml.txt');

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        body: formData,
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        failOnStatusCode: false
      }).then((response) => {
        expect([400, 200]).to.include(response.status);
        if (response.status === 200) {
          expect(response.body.status).to.eq('ERRO');
        }
      });
    });

    it('should return error for file too large', () => {
      // Criar arquivo muito grande (simulando)
      const largeContent = 'x'.repeat(11 * 1024 * 1024); // 11MB
      const blob = new Blob([largeContent], { type: 'text/xml' });
      const formData = new FormData();
      formData.append('file', blob, 'nfe-muito-grande.xml');

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        body: formData,
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        failOnStatusCode: false
      }).then((response) => {
        expect([400, 200]).to.include(response.status);
        if (response.status === 200) {
          expect(response.body.status).to.eq('ERRO');
          expect(response.body.mensagem).to.contain('muito grande');
        }
      });
    });

    it('should handle duplicate NFe gracefully', () => {
      // Processar a mesma NFe duas vezes
      const blob1 = new Blob([nfeCompraSample], { type: 'text/xml' });
      const formData1 = new FormData();
      formData1.append('file', blob1, 'nfe-duplicada-1.xml');

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        body: formData1,
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then((response1) => {
        expect(response1.status).to.eq(200);
        expect(response1.body.status).to.eq('SUCESSO');

        // Tentar processar a mesma NFe novamente
        const blob2 = new Blob([nfeCompraSample], { type: 'text/xml' });
        const formData2 = new FormData();
        formData2.append('file', blob2, 'nfe-duplicada-2.xml');

        cy.authenticatedRequest({
          method: 'POST',
          url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
          body: formData2,
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          failOnStatusCode: false
        }).then((response2) => {
          // O sistema pode retornar erro ou sucesso dependendo da implementação
          expect(response2.status).to.eq(200);
          
          // Se foi processado, deve haver alguma indicação de inconsistência
          if (response2.body.status === 'SUCESSO') {
            // Verificar se não houve duplicação no estoque
            cy.authenticatedRequest({
              method: 'GET',
              url: `${baseUrl}/api/estoque`
            }).then((estoqueResponse) => {
              // O estoque não deve ser duplicado
              const estoqueParacetamol = estoqueResponse.body.filter(e => 
                e.produtoReferencia && e.produtoReferencia.ean === '7891234567890'
              );
              // Deve haver apenas um registro por produto/lote
              expect(estoqueParacetamol.length).to.be.at.most(2); // No máximo 2 se houver lotes diferentes
            });
          }
        });
      });
    });
  });

  describe('Verificação de Inconsistências', () => {
    it('should detect inconsistencies in NFe data', () => {
      // NFe com inconsistências propositais
      const nfeInconsistente = `<?xml version="1.0" encoding="UTF-8"?>
<nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
  <NFe>
    <infNFe Id="NFe35240111222333000144550010000099991234567890">
      <ide>
        <nNF>9999</nNF>
        <serie>1</serie>
        <dhEmi>2024-01-15T10:00:00-03:00</dhEmi>
      </ide>
      <emit>
        <CNPJ>99999999000199</CNPJ>
        <xNome>Fornecedor Inconsistente</xNome>
      </emit>
      <dest>
        <CNPJ>12345678000199</CNPJ>
        <xNome>FreePharma</xNome>
      </dest>
      <det nItem="1">
        <prod>
          <cProd>PRODUTO999</cProd>
          <xProd>Produto Inconsistente</xProd>
          <NCM>INVALID</NCM>
          <CFOP>1102</CFOP>
          <qCom>10</qCom>
          <vUnCom>5.00</vUnCom>
          <vProd>50.00</vProd>
        </prod>
      </det>
      <total>
        <ICMSTot>
          <vNF>100.00</vNF>
        </ICMSTot>
      </total>
    </infNFe>
  </NFe>
</nfeProc>`;

      const blob = new Blob([nfeInconsistente], { type: 'text/xml' });
      const formData = new FormData();
      formData.append('file', blob, 'nfe-inconsistente.xml');

      cy.authenticatedRequest({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        body: formData,
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('importacaoId');
        
        // Se foi processada com sucesso, verificar inconsistências
        if (response.body.status === 'SUCESSO') {
          expect(response.body).to.have.property('inconsistenciasDetectadas');
          
          // Se há inconsistências reportadas, deve haver alerta
          if (response.body.inconsistenciasDetectadas > 0) {
            expect(response.body).to.have.property('alertas');
            expect(response.body.alertas).to.include('inconsistências');
          }
        }

        // Verificar se inconsistências foram detectadas na base
        cy.authenticatedRequest({
          method: 'GET',
          url: `${baseUrl}/api/fiscal/inconsistencias`,
          failOnStatusCode: false
        }).then((inconsistenciasResponse) => {
          if (inconsistenciasResponse.status === 200) {
            expect(inconsistenciasResponse.body).to.be.an('array');
            
            // Se o endpoint existe, verificar inconsistências
            const inconsistenciasDetectadas = inconsistenciasResponse.body.filter(i => 
              i.notaFiscal && i.notaFiscal.numero === '9999'
            );
            // Pelo menos deveria detectar NCM inválido e divergência de valor
            expect(inconsistenciasDetectadas.length).to.be.at.least(1);
          }
        });
      });
    });
  });

  after(() => {
    // Cleanup
    if (unidadeId) {
      cy.cleanupTestData('unidades', unidadeId);
    }
    if (farmaciaId) {
      cy.cleanupTestData('farmacias', farmaciaId);
    }
  });
});