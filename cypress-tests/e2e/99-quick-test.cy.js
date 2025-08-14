describe('Quick API Test', () => {
  const baseUrl = Cypress.env('baseUrl') || 'http://localhost:9876';
  
  it('should access farmacias endpoint with auto-login', () => {
    cy.authenticatedRequest({
      method: 'GET',
      url: `${baseUrl}/api/farmacias`
    }).then((response) => {
      expect(response.status).to.eq(200);
      expect(response.body).to.be.an('array');
      
      if (response.body.length > 0) {
        expect(response.body[0]).to.have.property('id');
        expect(response.body[0]).to.have.property('razaoSocial');
      }
    });
  });
  
  it('should access fornecedores endpoint with auto-login', () => {
    cy.authenticatedRequest({
      method: 'GET',
      url: `${baseUrl}/api/fornecedores`
    }).then((response) => {
      expect(response.status).to.eq(200);
      expect(response.body).to.be.an('array');
    });
  });
});