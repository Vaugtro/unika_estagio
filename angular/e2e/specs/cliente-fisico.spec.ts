import { test, expect } from '@playwright/test'
import { HomePage } from '../pages/home.page'
import { FisicoTablePage } from '../pages/fisico-table.page'
import { FisicoDetailPage } from '../pages/fisico-detail.page'
import { CreateFisicoDialog } from '../pages/create-fisico-dialog.page'
import { ConfirmDialog } from '../pages/confirm-dialog.page'
import { setupClientesFisicosMocks, mockClientesFisicos } from '../fixtures/clientes-fisicos'
import { isRealApi } from '../helpers/test-mode'
import { setupRealApiData, PF_CLIENTS } from '../helpers/test-data'

test.describe('Cliente Físico CRUD', () => {
  let home: HomePage
  let fisicoTable: FisicoTablePage
  const useRealApi = isRealApi()

  test.beforeAll(async ({ request }) => {
    if (useRealApi) {
      await setupRealApiData(request)
    }
  })

  test.beforeEach(async ({ page }) => {
    await page.unrouteAll()
    setupClientesFisicosMocks(page)
    home = new HomePage(page)
    fisicoTable = new FisicoTablePage(page)
    await home.goto()
    await home.selectFisicoTab()
    await fisicoTable.waitForLoad()
  })

  test('should create a new PF client', async ({ page }) => {
    await fisicoTable.clickNovo()
    const dialog = new CreateFisicoDialog(page)
    await expect(dialog.dialogTitle).toHaveText('Novo Cliente Físico')

    await dialog.fillForm({
      cpf: '876.477.910-60',
      nome: 'Novo Cliente Teste',
      rg: '123456789',
      email: 'novo@teste.com',
      dataNascimento: '1990-05-10',
      endereco: {
        logradouro: 'Rua Teste',
        numero: '123',
        bairro: 'Centro',
        cep: '01001000',
        telefone: '(11) 91234-5678',
        ufNome: 'São Paulo',
        municipioNome: 'São Paulo',
      },
    })

    await dialog.clickSalvar()
    await page.waitForLoadState('networkidle')

    const toast = page.locator('.toast-success')
    await expect(toast).toContainText('criado com sucesso', { timeout: 5000 })
  })

  test('should create a new PF client with 3 enderecos', async ({ page }) => {
    await fisicoTable.clickNovo()
    const dialog = new CreateFisicoDialog(page)
    await expect(dialog.dialogTitle).toHaveText('Novo Cliente Físico')

    await dialog.fillForm({
      cpf: '320.161.980-95',
      nome: 'Multi Endereco PF',
      rg: '987654321',
      email: 'multi@pf.com',
      dataNascimento: '1985-03-15',
      endereco: {
        logradouro: 'Rua A',
        numero: '100',
        bairro: 'Centro',
        cep: '01001000',
        telefone: '(11) 91234-5678',
        ufNome: 'São Paulo',
        municipioNome: 'São Paulo',
      },
    })

    const addBtn = page.locator('app-endereco-form').getByRole('button', { name: 'Adicionar endereço' })

    await addBtn.click()
    await page.waitForTimeout(300)

    await page.locator('app-endereco-form').getByLabel('Logradouro').nth(1).fill('Rua B')
    await page.locator('app-endereco-form').getByLabel('Número').nth(1).fill('200')
    await page.locator('app-endereco-form').getByLabel('Bairro').nth(1).fill('Jardim')
    await page.locator('app-endereco-form').getByLabel('CEP').nth(1).fill('02002000')
    await page.locator('app-endereco-form').getByLabel('Telefone').nth(1).fill('(11) 99876-5432')
    await page.locator('app-endereco-form').getByLabel('Estado').nth(1).click()
    await page.locator('mat-option', { hasText: 'São Paulo' }).first().waitFor({ state: 'visible', timeout: 5000 })
    await page.locator('mat-option', { hasText: 'São Paulo' }).first().click()
    await page.locator('app-endereco-form').getByLabel('Município').nth(1).click()
    await page.locator('mat-option', { hasText: 'São Paulo' }).last().waitFor({ state: 'visible', timeout: 5000 })
    await page.locator('mat-option', { hasText: 'São Paulo' }).last().click()

    await addBtn.click()
    await page.waitForTimeout(300)

    await page.locator('app-endereco-form').getByLabel('Logradouro').nth(2).fill('Rua C')
    await page.locator('app-endereco-form').getByLabel('Número').nth(2).fill('300')
    await page.locator('app-endereco-form').getByLabel('Bairro').nth(2).fill('Vila Nova')
    await page.locator('app-endereco-form').getByLabel('CEP').nth(2).fill('03003000')
    await page.locator('app-endereco-form').getByLabel('Telefone').nth(2).fill('(11) 97777-1111')
    await page.locator('app-endereco-form').getByLabel('Estado').nth(2).click()
    await page.locator('mat-option', { hasText: 'São Paulo' }).first().waitFor({ state: 'visible', timeout: 5000 })
    await page.locator('mat-option', { hasText: 'São Paulo' }).first().click()
    await page.locator('app-endereco-form').getByLabel('Município').nth(2).click()
    await page.locator('mat-option', { hasText: 'São Paulo' }).last().waitFor({ state: 'visible', timeout: 5000 })
    await page.locator('mat-option', { hasText: 'São Paulo' }).last().click()

    await dialog.clickSalvar()
    await page.waitForLoadState('networkidle')

    const toast = page.locator('.toast-success')
    await expect(toast).toContainText('criado com sucesso', { timeout: 5000 })
  })

  test('should enforce single principal address constraint', async ({ page }) => {
    await fisicoTable.clickNovo()
    const dialog = new CreateFisicoDialog(page)
    await expect(dialog.dialogTitle).toHaveText('Novo Cliente Físico')

    await dialog.fillForm({
      cpf: '588.109.000-49',
      nome: 'Principal Test PF',
      rg: '555555555',
      email: 'principal@test.com',
      dataNascimento: '1990-01-01',
      endereco: {
        logradouro: 'Rua Principal',
        numero: '1',
        bairro: 'Centro',
        cep: '01001000',
        telefone: '(11) 91234-5678',
        ufNome: 'São Paulo',
        municipioNome: 'São Paulo',
      },
    })

    const addBtn = page.locator('app-endereco-form').getByRole('button', { name: 'Adicionar endereço' })
    await addBtn.click()
    await page.waitForTimeout(300)

    await page.locator('app-endereco-form').getByLabel('Logradouro').nth(1).fill('Rua Secundaria')
    await page.locator('app-endereco-form').getByLabel('Número').nth(1).fill('2')
    await page.locator('app-endereco-form').getByLabel('Bairro').nth(1).fill('Jardim')
    await page.locator('app-endereco-form').getByLabel('CEP').nth(1).fill('02002000')
    await page.locator('app-endereco-form').getByLabel('Telefone').nth(1).fill('(11) 99876-5432')
    await page.locator('app-endereco-form').getByLabel('Estado').nth(1).click()
    await page.locator('mat-option', { hasText: 'São Paulo' }).first().waitFor({ state: 'visible', timeout: 5000 })
    await page.locator('mat-option', { hasText: 'São Paulo' }).first().click()
    await page.locator('app-endereco-form').getByLabel('Município').nth(1).click()
    await page.locator('mat-option', { hasText: 'São Paulo' }).last().waitFor({ state: 'visible', timeout: 5000 })
    await page.locator('mat-option', { hasText: 'São Paulo' }).last().click()

    await page.locator('app-endereco-form .mat-checkbox-layout').nth(1).click()
    await page.waitForTimeout(500)

    await expect(page.locator('app-endereco-form input[type="checkbox"]').first()).not.toBeChecked()
    await expect(page.locator('app-endereco-form input[type="checkbox"]').nth(1)).toBeChecked()

    await dialog.clickSalvar()
    await page.waitForLoadState('networkidle')

    const successToast = page.locator('.toast-success')
    await expect(successToast).toContainText('criado com sucesso', { timeout: 5000 })
  })

  test('should search PF clients by name', async () => {
    await fisicoTable.search('Maria')
    await fisicoTable.waitForLoad()
    const rows = await fisicoTable.getRowText(0)
    expect(rows).toContain(PF_CLIENTS[1].nome)
  })

  test('should search PF clients by CPF', async () => {
    await fisicoTable.search(PF_CLIENTS[2].cpf)
    await fisicoTable.waitForLoad()
    const rows = await fisicoTable.getRowText(0)
    expect(rows).toContain(PF_CLIENTS[2].nome)
  })

  test('should clear search and reload all clients', async () => {
    await fisicoTable.search('xyznonexistent')
    await fisicoTable.waitForLoad()
    expect(await fisicoTable.getRowCount()).toBe(0)

    await fisicoTable.clearSearch()
    await fisicoTable.waitForLoad()
    expect(await fisicoTable.getRowCount()).toBeGreaterThan(0)
  })

  test('should navigate to PF detail page', async ({ page }) => {
    await fisicoTable.clickDetalhes(0)
    await page.waitForLoadState('networkidle')
    expect(page.url()).toContain('/fisico/')

    const detail = new FisicoDetailPage(page)
    await detail.waitForLoad()
    const cardText = await detail.getInfoCardText()
    expect(cardText).toContain(PF_CLIENTS[0].nome)
  })

  test('should open edit dialog from table', async ({ page }) => {
    await fisicoTable.clickEditar(1)
    await expect(page.locator('h2[mat-dialog-title]')).toHaveText('Editar Cliente Físico')
  })

  test('should toggle PF client status to inactive', async ({ page }) => {
    await fisicoTable.clickToggleStatus(0)
    await page.waitForLoadState('networkidle')

    const toast = page.locator('.toast-success')
    await expect(toast).toContainText('inativado', { timeout: 5000 })
  })

  test('should toggle PF client status to active', async ({ page }) => {
    await fisicoTable.clickToggleStatus(2)
    await page.waitForLoadState('networkidle')

    const toast = page.locator('.toast-success')
    await expect(toast).toContainText('ativado', { timeout: 5000 })
  })

  test('should delete inactive PF client from detail page', async ({ page }) => {
    await fisicoTable.search('Carlos Inativo')
    await fisicoTable.waitForLoad()
    await fisicoTable.clickDetalhes(0)

    const detail = new FisicoDetailPage(page)
    await detail.waitForLoad()
    await expect(detail.excluirButton).toBeVisible()

    await detail.clickExcluir()
    const confirm = new ConfirmDialog(page)
    await expect(confirm.dialogTitle).toHaveText('Excluir Cliente Físico')
    await confirm.confirm()
    await page.waitForLoadState('networkidle')

    const toast = page.locator('.toast-success')
    await expect(toast).toContainText('excluído permanentemente', { timeout: 5000 })

    expect(page.url()).toContain('/home')
  })

  test('should open export dialog', async ({ page }) => {
    await fisicoTable.clickExportar()
    await expect(page.locator('h2[mat-dialog-title]')).toHaveText('Exportar Clientes Físicos')
  })

  test('should import PF clients from file', async ({ page }) => {
    page.route('**/v1/export/clientes/fisicos/import', async (route) => {
      if (route.request().method() === 'POST') {
        await route.fulfill({ status: 200, json: { successCount: 3, errors: [] } })
      }
    })

    await fisicoTable.clickImportar()
    await expect(page.locator('h2[mat-dialog-title]')).toHaveText('Importar Clientes Físicos')

    await page.locator('input[type="file"]').setInputFiles({
      name: 'clientes.xlsx',
      mimeType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      buffer: Buffer.from('fake'),
    })

    await page.getByRole('button', { name: /^Importar$/ }).click()
    await page.waitForLoadState('networkidle')

    const toast = page.locator('.toast-success')
    await expect(toast).toContainText('importados com sucesso', { timeout: 5000 })
  })

  test('should show error toast on API failure', async ({ page }) => {
    test.skip(useRealApi, 'Skipped in real API mode')

    page.route('**/v1/clientes/fisicos?*', async (route) => {
      await route.fulfill({ status: 500 })
    })
    await page.reload()
    await page.waitForLoadState('networkidle')

    const toast = page.locator('.toast-error')
    await expect(toast).toContainText('Erro ao carregar clientes', { timeout: 5000 })
  })
})
