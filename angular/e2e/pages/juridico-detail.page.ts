import { Page, Locator } from '@playwright/test'

export class JuridicoDetailPage {
  readonly page: Page
  readonly loadingSpinner: Locator
  readonly voltarLink: Locator
  readonly editarButton: Locator
  readonly excluirButton: Locator
  readonly infoCard: Locator

  constructor(page: Page) {
    this.page = page
    this.loadingSpinner = page.locator('mat-spinner')
    this.voltarLink = page.getByRole('link', { name: 'Voltar' })
    this.editarButton = page.getByRole('button', { name: 'Editar' })
    this.excluirButton = page.getByRole('button', { name: 'Excluir Cliente' })
    this.infoCard = page.locator('app-juridico-info-card')
  }

  async goto(id: number) {
    await this.page.goto(`/juridico/${id}`)
    await this.waitForLoad()
  }

  async waitForLoad() {
    await this.loadingSpinner.waitFor({ state: 'hidden', timeout: 10000 }).catch(() => {})
    await this.page.waitForLoadState('networkidle')
  }

  async clickEditar() {
    await this.editarButton.click()
  }

  async clickExcluir() {
    await this.excluirButton.click()
  }

  async getInfoCardText() {
    return await this.infoCard.textContent()
  }
}
