import { test, expect } from '@playwright/test'
import { HomePage } from '../pages/home.page'
import { JuridicoTablePage } from '../pages/juridico-table.page'
import { JuridicoDetailPage } from '../pages/juridico-detail.page'
import { CreateJuridicoDialog } from '../pages/create-juridico-dialog.page'
import { ConfirmDialog } from '../pages/confirm-dialog.page'
import { setupClientesJuridicosMocks, mockClientesJuridicos } from '../fixtures/clientes-juridicos'

test.describe('Cliente Jurídico CRUD', () => {
  let home: HomePage
  let juridicoTable: JuridicoTablePage

  test.beforeEach(async ({ page }) => {
    await page.unrouteAll()
    setupClientesJuridicosMocks(page)
    home = new HomePage(page)
    juridicoTable = new JuridicoTablePage(page)
    await home.goto()
    await home.selectJuridicoTab()
    await juridicoTable.waitForLoad()
  })

  test('should display PJ table with client data', async () => {
    const rowCount = await juridicoTable.getRowCount()
    expect(rowCount).toBe(mockClientesJuridicos.length)

    const firstRow = await juridicoTable.getRowText(0)
    expect(firstRow).toContain('Empresa Alpha Ltda')
    expect(firstRow).toContain('11.222.333/0001-44')
  })

  test('should display PJ table columns', async ({ page }) => {
    const headerRow = page.locator('tr[mat-header-row]')
    await expect(headerRow).toContainText('ID')
    await expect(headerRow).toContainText('Razão Social')
    await expect(headerRow).toContainText('CNPJ')
    await expect(headerRow).toContainText('E-mail')
    await expect(headerRow).toContainText('Status')
  })

  test('should create a new PJ client', async ({ page }) => {
    await juridicoTable.clickNovo()
    const dialog = new CreateJuridicoDialog(page)
    await expect(dialog.dialogTitle).toHaveText('Novo Cliente Jurídico')

    await dialog.fillForm({
      cnpj: '11.444.777/0001-61',
      razaoSocial: 'Empresa Nova Ltda',
      inscricaoEstadual: '123456789',
      email: 'nova@empresa.com',
      dataCriacaoEmpresa: '2000-01-15',
      endereco: {
        logradouro: 'Av Comercial',
        numero: '500',
        bairro: 'Centro',
        cep: '02002000',
        telefone: '(11) 99876-5432',
        ufNome: 'São Paulo',
        municipioNome: 'São Paulo',
      },
    })

    await dialog.clickSalvar()
    await page.waitForLoadState('networkidle')

    const toast = page.locator('.toast-success')
    await expect(toast).toContainText('criado com sucesso', { timeout: 5000 })
  })

  test('should create a new PJ client with 3 enderecos', async ({ page }) => {
    await juridicoTable.clickNovo()
    const dialog = new CreateJuridicoDialog(page)
    await expect(dialog.dialogTitle).toHaveText('Novo Cliente Jurídico')

    await dialog.fillForm({
      cnpj: '11.444.777/0001-61',
      razaoSocial: 'Multi Endereco PJ Ltda',
      inscricaoEstadual: '987654321',
      email: 'multi@pj.com',
      dataCriacaoEmpresa: '1995-07-20',
      endereco: {
        logradouro: 'Av X',
        numero: '1000',
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

    await page.locator('app-endereco-form').getByLabel('Logradouro').nth(1).fill('Av Y')
    await page.locator('app-endereco-form').getByLabel('Número').nth(1).fill('2000')
    await page.locator('app-endereco-form').getByLabel('Bairro').nth(1).fill('Jardim')
    await page.locator('app-endereco-form').getByLabel('CEP').nth(1).fill('02002000')
    await page.locator('app-endereco-form').getByLabel('Telefone').nth(1).fill('(11) 99876-5432')
    await page.locator('app-endereco-form').getByLabel('Estado').nth(1).click()
    await page.locator('mat-option', { hasText: 'São Paulo' }).first().click()
    await page.locator('app-endereco-form').getByLabel('Município').nth(1).click()
    await page.locator('mat-option', { hasText: 'São Paulo' }).last().click()

    await addBtn.click()
    await page.waitForTimeout(300)

    await page.locator('app-endereco-form').getByLabel('Logradouro').nth(2).fill('Av Z')
    await page.locator('app-endereco-form').getByLabel('Número').nth(2).fill('3000')
    await page.locator('app-endereco-form').getByLabel('Bairro').nth(2).fill('Vila Nova')
    await page.locator('app-endereco-form').getByLabel('CEP').nth(2).fill('03003000')
    await page.locator('app-endereco-form').getByLabel('Telefone').nth(2).fill('(11) 97777-1111')
    await page.locator('app-endereco-form').getByLabel('Estado').nth(2).click()
    await page.locator('mat-option', { hasText: 'São Paulo' }).first().click()
    await page.locator('app-endereco-form').getByLabel('Município').nth(2).click()
    await page.locator('mat-option', { hasText: 'São Paulo' }).last().click()

    await dialog.clickSalvar()
    await page.waitForLoadState('networkidle')

    const toast = page.locator('.toast-success')
    await expect(toast).toContainText('criado com sucesso', { timeout: 5000 })
  })

  test('should enforce single principal address constraint', async ({ page }) => {
    await juridicoTable.clickNovo()
    const dialog = new CreateJuridicoDialog(page)
    await expect(dialog.dialogTitle).toHaveText('Novo Cliente Jurídico')

    await dialog.fillForm({
      cnpj: '11.444.777/0001-61',
      razaoSocial: 'Principal Test PJ Ltda',
      inscricaoEstadual: '123456789',
      email: 'principal@pjtest.com',
      dataCriacaoEmpresa: '2000-01-01',
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
    await page.locator('mat-option', { hasText: 'São Paulo' }).first().click()
    await page.locator('app-endereco-form').getByLabel('Município').nth(1).click()
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

  test('should search PJ clients by razao social', async () => {
    await juridicoTable.search('Beta')
    await juridicoTable.waitForLoad()
    const rows = await juridicoTable.getRowText(0)
    expect(rows).toContain('Beta Comércio')
  })

  test('should search PJ clients by CNPJ', async () => {
    await juridicoTable.search('55.666.777/0001-88')
    await juridicoTable.waitForLoad()
    const rows = await juridicoTable.getRowText(0)
    expect(rows).toContain('Beta Comércio')
  })

  test('should clear search and reload all PJ clients', async () => {
    await juridicoTable.search('xyznonexistent')
    await juridicoTable.waitForLoad()
    expect(await juridicoTable.getRowCount()).toBe(0)

    await juridicoTable.clearSearch()
    await juridicoTable.waitForLoad()
    expect(await juridicoTable.getRowCount()).toBe(mockClientesJuridicos.length)
  })

  test('should navigate to PJ detail page', async ({ page }) => {
    await juridicoTable.clickDetalhes(0)
    await page.waitForLoadState('networkidle')
    expect(page.url()).toContain('/juridico/1')

    const detail = new JuridicoDetailPage(page)
    await detail.waitForLoad()
    const cardText = await detail.getInfoCardText()
    expect(cardText).toContain('Empresa Alpha Ltda')
  })

  test('should open edit dialog from table', async ({ page }) => {
    await juridicoTable.clickEditar(1)
    await page.waitForLoadState('networkidle')
    const dialogTitle = page.locator('h2[mat-dialog-title]')
    await expect(dialogTitle).toHaveText('Editar Cliente Jurídico')
  })

  test('should toggle PJ client status', async ({ page }) => {
    await juridicoTable.clickToggleStatus(0)
    await page.waitForLoadState('networkidle')

    const toast = page.locator('.toast-success')
    await expect(toast).toContainText('inativado', { timeout: 5000 })
  })

  test('should activate inactive PJ client', async ({ page }) => {
    await juridicoTable.clickToggleStatus(2)
    await page.waitForLoadState('networkidle')

    const toast = page.locator('.toast-success')
    await expect(toast).toContainText('ativado', { timeout: 5000 })
  })

  test('should delete inactive PJ client from detail page', async ({ page }) => {
    const detail = new JuridicoDetailPage(page)
    await detail.goto(3)
    await detail.waitForLoad()

    if (await detail.excluirButton.isVisible()) {
      await detail.clickExcluir()
      const confirm = new ConfirmDialog(page)
      await expect(confirm.dialogTitle).toHaveText('Excluir Cliente Jurídico')
      await confirm.confirm()
      await page.waitForLoadState('networkidle')

      const toast = page.locator('.toast-success')
      await expect(toast).toContainText('excluído permanentemente', { timeout: 5000 })
      expect(page.url()).toContain('/home')
    }
  })

  test('should open export dialog', async ({ page }) => {
    await juridicoTable.clickExportar()
    await expect(page.locator('h2[mat-dialog-title]')).toHaveText('Exportar Clientes Jurídicos')
  })

  test('should import PJ clients from file', async ({ page }) => {
    page.route('**/v1/export/clientes/juridicos/import', async (route) => {
      if (route.request().method() === 'POST') {
        await route.fulfill({ status: 200, json: { successCount: 3, errors: [] } })
      }
    })

    await juridicoTable.clickImportar()
    await expect(page.locator('h2[mat-dialog-title]')).toHaveText('Importar Clientes Jurídicos')

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
})
