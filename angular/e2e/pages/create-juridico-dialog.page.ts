import { Page, Locator } from '@playwright/test'

export class CreateJuridicoDialog {
  readonly page: Page
  readonly dialogTitle: Locator
  readonly cnpjInput: Locator
  readonly razaoSocialInput: Locator
  readonly inscricaoEstadualInput: Locator
  readonly emailInput: Locator
  readonly dataCriacaoEmpresaInput: Locator
  readonly logradouroInput: Locator
  readonly numeroInput: Locator
  readonly bairroInput: Locator
  readonly cepInput: Locator
  readonly telefoneInput: Locator
  readonly ufSelect: Locator
  readonly municipioSelect: Locator
  readonly principalCheckbox: Locator
  readonly salvarButton: Locator
  readonly cancelarButton: Locator

  constructor(page: Page) {
    this.page = page
    this.dialogTitle = page.locator('h2[mat-dialog-title]')

    this.cnpjInput = page.locator('mat-dialog-content').locator('input[mask="00.000.000/0000-00"]')
    this.razaoSocialInput = page.locator('mat-dialog-content').getByLabel('Razão Social')
    this.inscricaoEstadualInput = page.locator('mat-dialog-content').getByLabel('Inscrição Estadual')
    this.emailInput = page.locator('mat-dialog-content').getByLabel('E-mail')
    this.dataCriacaoEmpresaInput = page.locator('mat-dialog-content').getByLabel('Data Criação')

    this.logradouroInput = page.locator('app-endereco-form').getByLabel('Logradouro')
    this.numeroInput = page.locator('app-endereco-form').getByLabel('Número')
    this.bairroInput = page.locator('app-endereco-form').getByLabel('Bairro')
    this.cepInput = page.locator('app-endereco-form').getByLabel('CEP')
    this.telefoneInput = page.locator('app-endereco-form').getByLabel('Telefone')
    this.ufSelect = page.locator('app-endereco-form').getByLabel('Estado')
    this.municipioSelect = page.locator('app-endereco-form').getByLabel('Município')
    this.principalCheckbox = page.locator('app-endereco-form').getByLabel('Principal')

    this.salvarButton = page.getByRole('button', { name: /^Salvar$/ })
    this.cancelarButton = page.getByRole('button', { name: 'Cancelar' })
  }

  async fillCNPJ(cnpj: string) {
    await this.cnpjInput.fill(cnpj)
  }

  async fillRazaoSocial(value: string) {
    await this.razaoSocialInput.fill(value)
  }

  async fillInscricaoEstadual(value: string) {
    await this.inscricaoEstadualInput.fill(value)
  }

  async fillEmail(email: string) {
    await this.emailInput.fill(email)
  }

  async fillDataCriacaoEmpresa(data: string) {
    await this.dataCriacaoEmpresaInput.fill(data)
  }

  async fillLogradouro(value: string) {
    await this.logradouroInput.fill(value)
  }

  async fillNumero(value: string) {
    await this.numeroInput.fill(value)
  }

  async fillBairro(value: string) {
    await this.bairroInput.fill(value)
  }

  async fillCEP(value: string) {
    await this.cepInput.fill(value)
  }

  async fillTelefone(value: string) {
    await this.telefoneInput.fill(value)
  }

  async selectUF(nome: string) {
    await this.ufSelect.click()
    await this.page.locator('mat-option', { hasText: nome }).first().waitFor({ state: 'visible', timeout: 5000 })
    await this.page.locator('mat-option', { hasText: nome }).first().click()
  }

  async selectMunicipio(nome: string) {
    await this.municipioSelect.click()
    await this.page.locator('mat-option', { hasText: nome }).last().waitFor({ state: 'visible', timeout: 5000 })
    await this.page.locator('mat-option', { hasText: nome }).last().click()
  }

  async setAsPrincipal() {
    if (!(await this.principalCheckbox.isChecked())) {
      await this.principalCheckbox.click()
    }
  }

  async clickSalvar() {
    await this.salvarButton.click()
  }

  async clickCancelar() {
    await this.cancelarButton.click()
  }

  async fillEndereco(data: { logradouro: string; numero: string; bairro: string; cep: string; telefone: string; ufNome: string; municipioNome: string }) {
    await this.fillLogradouro(data.logradouro)
    await this.fillNumero(data.numero)
    await this.fillBairro(data.bairro)
    await this.fillCEP(data.cep)
    await this.fillTelefone(data.telefone)
    await this.selectUF(data.ufNome)
    await this.selectMunicipio(data.municipioNome)
    await this.setAsPrincipal()
  }

  async fillForm(data: { cnpj: string; razaoSocial: string; inscricaoEstadual?: string; email?: string; dataCriacaoEmpresa?: string; endereco: { logradouro: string; numero: string; bairro: string; cep: string; telefone: string; ufNome: string; municipioNome: string } }) {
    await this.fillCNPJ(data.cnpj)
    await this.fillRazaoSocial(data.razaoSocial)
    if (data.inscricaoEstadual) await this.fillInscricaoEstadual(data.inscricaoEstadual)
    if (data.email) await this.fillEmail(data.email)
    if (data.dataCriacaoEmpresa) await this.fillDataCriacaoEmpresa(data.dataCriacaoEmpresa)
    await this.fillEndereco(data.endereco)
  }
}
