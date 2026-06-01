import { Page, Locator } from '@playwright/test'

export class HomePage {
  readonly page: Page
  readonly fisicoTab: Locator
  readonly juridicoTab: Locator

  constructor(page: Page) {
    this.page = page
    this.fisicoTab = page.getByRole('button', { name: 'Físicos' })
    this.juridicoTab = page.getByRole('button', { name: 'Jurídicos' })
  }

  async goto() {
    await this.page.goto('/home')
    await this.page.waitForLoadState('networkidle')
  }

  async selectFisicoTab() {
    await this.fisicoTab.click()
    await this.page.waitForLoadState('networkidle')
  }

  async selectJuridicoTab() {
    await this.juridicoTab.click()
    await this.page.waitForLoadState('networkidle')
  }
}
