import { test, expect } from '@playwright/test'
import { HomePage } from '../pages/home.page'
import { FisicoTablePage } from '../pages/fisico-table.page'
import { setupClientesFisicosMocks, mockClientesFisicos } from '../fixtures/clientes-fisicos'
import { resetEnderecos } from '../fixtures/enderecos'
import { PF_CLIENTS } from '../helpers/test-data'

test.describe('Home Page', () => {
  let home: HomePage
  let fisicoTable: FisicoTablePage

  test.beforeEach(async ({ page }) => {
    await page.unrouteAll()
    setupClientesFisicosMocks(page)
    home = new HomePage(page)
    fisicoTable = new FisicoTablePage(page)
    await home.goto()
  })

  test('should display PF tab as active by default', async () => {
    await expect(home.fisicoTab).toHaveClass(/tab-active/)
  })

  test('should switch to PJ tab when clicked', async ({ page }) => {
    await home.selectJuridicoTab()
    await expect(home.juridicoTab).toHaveClass(/tab-active/)
    await expect(page.locator('app-juridico-table')).toBeVisible()
  })

  test('should display PF table with client data', async () => {
    await fisicoTable.waitForLoad()
    expect(await fisicoTable.getRowCount()).toBeGreaterThan(0)

    const firstRow = await fisicoTable.getRowText(0)
    expect(firstRow).toContain('João Silva')
    expect(firstRow).toContain(PF_CLIENTS[0].cpf)
  })

  test('should display PF table columns', async ({ page }) => {
    await fisicoTable.waitForLoad()
    const headerRow = page.locator('tr[mat-header-row]')
    await expect(headerRow).toContainText('ID')
    await expect(headerRow).toContainText('Nome')
    await expect(headerRow).toContainText('CPF')
    await expect(headerRow).toContainText('E-mail')
    await expect(headerRow).toContainText('Status')
  })

  test('should show loading spinner while fetching data', async ({ page }) => {
    setupClientesFisicosMocks(page)
    fisicoTable = new FisicoTablePage(page)
    await page.goto('/home')
    await page.waitForLoadState('networkidle')
    await expect(fisicoTable.loadingSpinner).toBeHidden()
  })
})
