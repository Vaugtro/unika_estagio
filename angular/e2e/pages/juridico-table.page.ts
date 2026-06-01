import { Page, Locator } from '@playwright/test'

export class JuridicoTablePage {
  readonly page: Page
  readonly searchInput: Locator
  readonly novoButton: Locator
  readonly exportarButton: Locator
  readonly importarButton: Locator
  readonly tableRows: Locator
  readonly paginator: Locator
  readonly loadingSpinner: Locator

  constructor(page: Page) {
    this.page = page
    this.searchInput = page.getByPlaceholder('Buscar por razão social, CNPJ ou e-mail')
    this.novoButton = page.getByRole('button', { name: 'Novo' })
    this.exportarButton = page.getByRole('button', { name: 'Exportar' })
    this.importarButton = page.getByRole('button', { name: 'Importar' })
    this.tableRows = page.locator('tr[mat-row]')
    this.paginator = page.locator('mat-paginator')
    this.loadingSpinner = page.locator('mat-spinner')
  }

  async waitForLoad() {
    await this.loadingSpinner.waitFor({ state: 'hidden', timeout: 10000 }).catch(() => {})
    await this.page.waitForLoadState('networkidle')
  }

  async search(query: string) {
    await this.searchInput.fill(query)
    await this.page.waitForTimeout(400)
    await this.waitForLoad()
  }

  async clearSearch() {
    await this.searchInput.clear()
    await this.page.waitForTimeout(400)
    await this.waitForLoad()
  }

  async getRowCount() {
    return await this.tableRows.count()
  }

  async getRowText(index: number) {
    return await this.tableRows.nth(index).textContent()
  }

  async clickDetalhes(rowIndex: number) {
    await this.tableRows.nth(rowIndex).getByRole('link', { name: 'Detalhes' }).click()
    await this.page.waitForLoadState('networkidle')
  }

  async clickEditar(rowIndex: number) {
    await this.tableRows.nth(rowIndex).getByRole('button', { name: 'Editar' }).click()
  }

  async clickToggleStatus(rowIndex: number) {
    const statusBtn = this.tableRows.nth(rowIndex).getByRole('button').filter({ hasText: /Ativo|Inativo/ })
    await statusBtn.click()
  }

  async clickNovo() {
    await this.novoButton.click()
  }

  async clickExportar() {
    await this.exportarButton.click()
  }

  async clickImportar() {
    await this.importarButton.click()
  }
}
