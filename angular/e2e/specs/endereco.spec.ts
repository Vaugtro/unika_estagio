import { test, expect } from '@playwright/test'
import { HomePage } from '../pages/home.page'
import { FisicoTablePage } from '../pages/fisico-table.page'
import { FisicoDetailPage } from '../pages/fisico-detail.page'
import { setupEnderecosMocks, resetEnderecos } from '../fixtures/enderecos'
import { setupClientesFisicosMocks } from '../fixtures/clientes-fisicos'
import { isRealApi } from '../helpers/test-mode'
import { setupRealApiData } from '../helpers/test-data'

test.describe('Endereço Management', () => {
  test.beforeAll(async ({ request }) => {
    if (isRealApi()) {
      const data = await setupRealApiData(request)
      const clienteId = data.pfIds[0]
      if (clienteId) {
        await request.post(`/v1/enderecos/clientes/${clienteId}`, { data: { logradouro: 'Rua dos Pinheiros', numero: 200, bairro: 'Bela Vista', cep: '02002000', municipioId: 3550308 } })
        await request.post(`/v1/enderecos/clientes/${clienteId}`, { data: { logradouro: 'Rua Santos', numero: 300, bairro: 'Jardins', cep: '03003000', municipioId: 3550308 } })
      }
    }
  })

  test.beforeEach(async ({ page }) => {
    await page.unrouteAll()
    resetEnderecos()
    setupClientesFisicosMocks(page)
    setupEnderecosMocks(page)

    const home = new HomePage(page)
    const fisicoTable = new FisicoTablePage(page)
    await home.goto()
    await home.selectFisicoTab()
    await fisicoTable.waitForLoad()
    await fisicoTable.clickDetalhes(0)
    const detail = new FisicoDetailPage(page)
    await detail.waitForLoad()
  })

  test('should display address table in PF detail page', async ({ page }) => {
    await expect(page.locator('app-endereco-table')).toBeVisible()
  })

  test('should list addresses for a client', async ({ page }) => {
    const rows = page.locator('app-endereco-table tr[mat-row]')
    await expect(rows.first()).toBeVisible()
  })

  test('should display address columns', async ({ page }) => {
    const headerRow = page.locator('app-endereco-table tr[mat-header-row]')
    await expect(headerRow).toContainText('Logradouro')
    await expect(headerRow).toContainText('Número')
    await expect(headerRow).toContainText('Bairro')
    await expect(headerRow).toContainText('Cidade')
    await expect(headerRow).toContainText('Estado')
    await expect(headerRow).toContainText('CEP')
    await expect(headerRow).toContainText('Principal')
  })

  test('should show principal status on first address', async ({ page }) => {
    const btn = page.locator('app-endereco-table tr[mat-row]').first().getByRole('button').filter({ hasText: 'Sim' })
    await expect(btn).toBeVisible()
  })

  test('should open create address dialog', async ({ page }) => {
    await page.locator('app-endereco-table').getByRole('button', { name: 'Novo' }).click()
    await expect(page.locator('h2[mat-dialog-title]')).toHaveText('Novo Endereço')
  })

  test('should create new address', async ({ page }) => {
    await page.locator('app-endereco-table').getByRole('button', { name: 'Novo' }).click()
    await page.locator('mat-dialog-content').getByLabel('Logradouro').fill('Rua Nova')
    await page.locator('mat-dialog-content').getByLabel('Número').fill('42')
    await page.locator('mat-dialog-content').getByLabel('Bairro').fill('Jardim Novo')
    await page.locator('mat-dialog-content').getByLabel('CEP').fill('03003000')
    await page.locator('mat-dialog-content').getByLabel('Telefone').fill('(11) 97777-8888')
    await page.locator('mat-dialog-content').getByLabel('Estado').click()
    await page.locator('mat-option', { hasText: 'São Paulo' }).waitFor({ state: 'visible', timeout: 5000 })
    await page.locator('mat-option', { hasText: 'São Paulo' }).click()
    await page.locator('mat-dialog-content').getByLabel('Município').click()
    await page.getByRole('option', { name: 'São Paulo' }).last().waitFor({ state: 'visible', timeout: 5000 })
    await page.getByRole('option', { name: 'São Paulo' }).last().click()
    await page.waitForTimeout(300)
    await page.getByRole('button', { name: /^Salvar$/ }).click()
    await page.waitForLoadState('networkidle')
    await expect(page.locator('.toast-success')).toContainText('sucesso', { timeout: 5000 })
  })

  test('should delete the principal address', async ({ page }) => {
    const firstRow = page.locator('app-endereco-table tr[mat-row]').first()
    await expect(firstRow).toContainText('Sim')
    await firstRow.getByRole('button', { name: 'Excluir' }).click()
    await expect(page.locator('h2[mat-dialog-title]')).toHaveText('Excluir endereço')
    await page.getByRole('button', { name: 'Confirmar' }).click()
    await page.waitForLoadState('networkidle')
    await expect(page.locator('.toast-success')).toContainText('Endereço excluído', { timeout: 5000 })
    const rem = page.locator('app-endereco-table tr[mat-row]')
    await expect(rem).toHaveCount(2, { timeout: 5000 })
    await expect(rem.first()).toContainText('Sim')
  })

  test('should block deleting the last principal address', async ({ page }) => {
    const rows = () => page.locator('app-endereco-table tr[mat-row]')
    await expect(rows()).toHaveCount(3)
    for (let i = 0; i < 2; i++) {
      await rows().first().getByRole('button', { name: 'Excluir' }).click()
      await page.getByRole('button', { name: 'Confirmar' }).click()
      await page.waitForLoadState('networkidle')
      await expect(page.locator('.toast-success').first()).toContainText('Endereço excluído', { timeout: 5000 })
    }
    await expect(rows()).toHaveCount(1)
    await rows().first().getByRole('button', { name: 'Excluir' }).click()
    await page.getByRole('button', { name: 'Confirmar' }).click()
    await page.waitForLoadState('networkidle')
    await expect(page.locator('.toast-error')).toContainText('Erro ao excluir endereço', { timeout: 5000 })
    await expect(rows()).toHaveCount(1)
  })

  test('should open export dialog from address table', async ({ page }) => {
    await page.locator('app-endereco-table').getByRole('button', { name: 'Exportar' }).click()
    await expect(page.locator('h2[mat-dialog-title]')).toHaveText(/Exportar/)
  })

  test('should import enderecos from file', async ({ page }) => {
    page.route('**/v1/export/clientes/fisicos/import', async (route) => {
      if (route.request().method() === 'POST') {
        await route.fulfill({ status: 200, json: { successCount: 3, errors: [] } })
      }
    })
    await page.locator('app-endereco-table').getByRole('button', { name: 'Importar' }).click()
    await expect(page.locator('h2[mat-dialog-title]')).toHaveText(/Importar/)
    await page.locator('input[type="file"]').setInputFiles({
      name: 'enderecos.xlsx',
      mimeType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      buffer: Buffer.from('fake'),
    })
    await page.getByRole('button', { name: /^Importar$/ }).click()
    await page.waitForLoadState('networkidle')
    await expect(page.locator('.toast-success')).toContainText('importados com sucesso', { timeout: 5000 })
  })
})
