import { Page, Locator } from '@playwright/test'

export class ConfirmDialog {
  readonly page: Page
  readonly dialogTitle: Locator
  readonly dialogContent: Locator
  readonly confirmButton: Locator
  readonly cancelButton: Locator

  constructor(page: Page) {
    this.page = page
    this.dialogTitle = page.locator('h2[mat-dialog-title]')
    this.dialogContent = page.locator('mat-dialog-content p')
    this.confirmButton = page.getByRole('button', { name: 'Excluir' })
    this.cancelButton = page.getByRole('button', { name: 'Cancelar' })
  }

  async confirm() {
    await this.confirmButton.click()
  }

  async cancel() {
    await this.cancelButton.click()
  }
}
