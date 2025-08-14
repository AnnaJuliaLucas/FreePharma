describe('Importacao NFe API Tests', () => {
  const baseUrl = Cypress.env('baseUrl') || 'http://localhost:9876';
  
  before(() => {
    cy.login();
  });

  describe('POST /api/fiscal/importacao-nfe/xml', () => {
    it('should successfully import valid NFe XML file', () => {
      // Create a simple XML file for testing
      const xmlContent = `<?xml version="1.0" encoding="UTF-8"?>
<nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
  <NFe>
    <infNFe Id="NFe35200714200166000187550010000000123123456789">
      <ide>
        <cUF>35</cUF>
        <cNF>23456789</cNF>
        <natOp>Venda de mercadoria</natOp>
        <mod>55</mod>
        <serie>1</serie>
        <nNF>123</nNF>
        <dhEmi>2020-07-01T10:00:00-03:00</dhEmi>
        <tpNF>1</tpNF>
        <idDest>1</idDest>
        <cMunFG>3550308</cMunFG>
        <tpImp>1</tpImp>
        <tpEmis>1</tpEmis>
        <cDV>9</cDV>
        <tpAmb>2</tpAmb>
        <finNFe>1</finNFe>
        <indFinal>1</indFinal>
        <indPres>1</indPres>
      </ide>
      <emit>
        <CNPJ>14200166000187</CNPJ>
        <xNome>Empresa Teste</xNome>
        <enderEmit>
          <xLgr>Rua Teste</xLgr>
          <nro>123</nro>
          <xBairro>Centro</xBairro>
          <cMun>3550308</cMun>
          <xMun>São Paulo</xMun>
          <UF>SP</UF>
          <CEP>01000000</CEP>
        </enderEmit>
      </emit>
      <dest>
        <CPF>12345678901</CPF>
        <xNome>Cliente Teste</xNome>
      </dest>
    </infNFe>
  </NFe>
</nfeProc>`;

      // Convert to blob
      const blob = new Blob([xmlContent], { type: 'text/xml' });
      const formData = new FormData();
      formData.append('file', blob, 'nfe-test.xml');

      const token = Cypress.env('accessToken') || 'test-token';

      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('status', 'SUCESSO');
        expect(response.body).to.have.property('arquivo', 'nfe-test.xml');
        expect(response.body).to.have.property('tamanho');
        expect(response.body).to.have.property('mensagem');
        expect(response.body).to.have.property('importacaoId');
        expect(response.body.mensagem).to.include('processada com sucesso');
      });
    });

    it('should return 400 for empty file', () => {
      const blob = new Blob([''], { type: 'text/xml' });
      const formData = new FormData();
      formData.append('file', blob, 'empty.xml');

      const token = Cypress.env('accessToken') || 'test-token';

      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
        expect(response.body).to.include('não pode ser vazio');
      });
    });

    it('should return 400 for non-XML file', () => {
      const blob = new Blob(['This is not XML content'], { type: 'text/plain' });
      const formData = new FormData();
      formData.append('file', blob, 'not-xml.txt');

      const token = Cypress.env('accessToken') || 'test-token';

      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
        expect(response.body).to.include('Apenas arquivos XML são aceitos');
      });
    });

    it('should return 400 for file without .xml extension', () => {
      const xmlContent = '<?xml version="1.0"?><root>test</root>';
      const blob = new Blob([xmlContent], { type: 'text/xml' });
      const formData = new FormData();
      formData.append('file', blob, 'file-without-xml-extension.txt');

      const token = Cypress.env('accessToken') || 'test-token';

      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
        expect(response.body).to.include('Apenas arquivos XML são aceitos');
      });
    });

    it('should return 400 for oversized file', () => {
      // Create a large XML content (over 10MB)
      const largeContent = '<?xml version="1.0"?><root>' + 'x'.repeat(11 * 1024 * 1024) + '</root>';
      const blob = new Blob([largeContent], { type: 'text/xml' });
      const formData = new FormData();
      formData.append('file', blob, 'large-file.xml');

      const token = Cypress.env('accessToken') || 'test-token';

      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
        expect(response.body).to.include('muito grande');
      });
    });

    it('should return 400 for missing file parameter', () => {
      const token = Cypress.env('accessToken') || 'test-token';

      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: new FormData(), // Empty form data
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
      });
    });

    it('should return 403 without authentication', () => {
      const xmlContent = '<?xml version="1.0"?><root>test</root>';
      const blob = new Blob([xmlContent], { type: 'text/xml' });
      const formData = new FormData();
      formData.append('file', blob, 'test.xml');

      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        body: formData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(403);
      });
    });

    it('should handle valid XML with different content types', () => {
      const xmlContent = `<?xml version="1.0" encoding="UTF-8"?>
<nfeProc>
  <NFe>
    <infNFe>
      <ide>
        <nNF>456</nNF>
        <natOp>Compra de mercadoria</natOp>
      </ide>
    </infNFe>
  </NFe>
</nfeProc>`;

      const blob = new Blob([xmlContent], { type: 'application/xml' });
      const formData = new FormData();
      formData.append('file', blob, 'nfe-application-xml.xml');

      const token = Cypress.env('accessToken') || 'test-token';

      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('status', 'SUCESSO');
        expect(response.body).to.have.property('arquivo', 'nfe-application-xml.xml');
        expect(response.body).to.have.property('importacaoId');
      });
    });

    it('should validate file name properly', () => {
      const xmlContent = '<?xml version="1.0"?><root>test</root>';
      const blob = new Blob([xmlContent], { type: 'text/xml' });
      const formData = new FormData();
      formData.append('file', blob, 'válid-file-nãme-çharacters.xml');

      const token = Cypress.env('accessToken') || 'test-token';

      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('status', 'SUCESSO');
      });
    });

    it('should handle NFe with validation errors and return structured error', () => {
      // NFe XML inválido (sem chave de acesso de 44 caracteres)
      const invalidNFeXml = `<?xml version="1.0" encoding="UTF-8"?>
<NFe>
  <infNFe Id="123">
    <ide>
      <nNF>123</nNF>
    </ide>
    <emit>
      <CNPJ>12345678000199</CNPJ>
      <xNome>Fornecedor Teste</xNome>
    </emit>
  </infNFe>
</NFe>`;

      const blob = new Blob([invalidNFeXml], { type: 'text/xml' });
      const formData = new FormData();
      formData.append('file', blob, 'nfe-chave-invalida.xml');

      const token = Cypress.env('accessToken') || 'test-token';

      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
        expect(response.body).to.have.property('status', 'ERRO');
        expect(response.body).to.have.property('mensagem');
        expect(response.body).to.have.property('timestamp');
        expect(response.body.mensagem).to.include('Chave de acesso inválida');
      });
    });

    it('should detect and report NFe with missing required fields', () => {
      // NFe XML sem emitente
      const nfeWithoutEmitente = `<?xml version="1.0" encoding="UTF-8"?>
<NFe>
  <infNFe Id="NFe12345678901234567890123456789012345678901234">
    <ide>
      <nNF>123</nNF>
    </ide>
  </infNFe>
</NFe>`;

      const blob = new Blob([nfeWithoutEmitente], { type: 'text/xml' });
      const formData = new FormData();
      formData.append('file', blob, 'nfe-sem-emitente.xml');

      const token = Cypress.env('accessToken') || 'test-token';

      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml`,
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData,
        failOnStatusCode: false
      }).then((response) => {
        expect(response.status).to.eq(400);
        expect(response.body).to.have.property('status', 'ERRO');
        expect(response.body.mensagem).to.include('emitente');
      });
    });
  });

  describe('POST /api/fiscal/importacao-nfe/xml/completo', () => {
    it('should accept additional parameters for complete validation', () => {
      const xmlContent = `<?xml version="1.0" encoding="UTF-8"?>
<NFe>
  <infNFe Id="NFe12345678901234567890123456789012345678901234">
    <ide>
      <nNF>456</nNF>
    </ide>
    <emit>
      <CNPJ>12345678000199</CNPJ>
      <xNome>Fornecedor Completo</xNome>
    </emit>
    <det nItem="1">
      <prod>
        <cProd>PROD001</cProd>
        <xProd>Produto Teste</xProd>
        <qCom>1</qCom>
        <vUnCom>10.00</vUnCom>
        <vProd>10.00</vProd>
      </prod>
    </det>
    <total>
      <ICMSTot>
        <vNF>10.00</vNF>
      </ICMSTot>
    </total>
  </infNFe>
</NFe>`;

      const blob = new Blob([xmlContent], { type: 'text/xml' });
      const formData = new FormData();
      formData.append('file', blob, 'nfe-completo.xml');
      formData.append('observacoes', 'Teste de importação completa');

      const token = Cypress.env('accessToken') || 'test-token';

      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/fiscal/importacao-nfe/xml/completo`,
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('status', 'SUCESSO');
        expect(response.body).to.have.property('importacaoId');
      });
    });
  });
});