describe(__filename, () => {
  it('Ensures the sample extension is installed in the OpenRefine instance', () => {
    // Visit the extension management page and verify that our application is listed
    cy.visit(Cypress.env('OPENREFINE_URL') + '/#manage-extensions')
    cy.get('#extensionList td:first-child')
      .filter(':contains("openrefine-sample-extension")')
      .should('have.length', 1);
  })
})
