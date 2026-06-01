import { Page } from '@playwright/test'

export const mockClientesJuridicos: any[] = [
  { id: 1, razaoSocial: 'Empresa Alpha Ltda', cnpj: '11.222.333/0001-44', email: 'alpha@empresa.com', nomeFantasia: 'Alpha', inscricaoEstadual: '123456789', estaAtivo: true, dataCriacaoEmpresa: '2000-01-01', dataCriacao: '2024-01-01T00:00:00', dataAtualizacao: '2024-06-01T00:00:00' },
  { id: 2, razaoSocial: 'Beta Comércio S.A.', cnpj: '55.666.777/0001-88', email: 'beta@comercio.com', nomeFantasia: 'Beta', inscricaoEstadual: '987654321', estaAtivo: true, dataCriacaoEmpresa: '1995-05-15', dataCriacao: '2024-02-01T00:00:00', dataAtualizacao: '2024-06-01T00:00:00' },
  { id: 3, razaoSocial: 'Gamma Inativa ME', cnpj: '99.888.777/0001-66', email: 'gamma@inativa.com', nomeFantasia: 'Gamma', inscricaoEstadual: '456789123', estaAtivo: false, dataCriacaoEmpresa: '2010-03-20', dataCriacao: '2024-03-01T00:00:00', dataAtualizacao: '2024-06-01T00:00:00' },
]

export function setupClientesJuridicosMocks(page: Page) {
  page.route('**/viacep.com.br/**', (route) => route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ erro: true }) }))

  page.route('**/v1/unidades-federativas', (route) =>
    route.fulfill({ json: [{ sigla: 'SP', nome: 'São Paulo' }, { sigla: 'RJ', nome: 'Rio de Janeiro' }] }))

  page.route('**/v1/municipios**', (route) =>
    route.fulfill({ json: [{ id: 3550308, nome: 'São Paulo' }, { id: 3509502, nome: 'Campinas' }] }))

  page.route(/\/v1\/clientes\/juridicos\/search(\?|$)/, async (route) => {
    const url = new URL(route.request().url())
    const q = url.searchParams.get('q') || ''
    const p = parseInt(url.searchParams.get('page') || '0')
    const s = parseInt(url.searchParams.get('size') || '10')
    const filtered = mockClientesJuridicos.filter(c =>
      c.razaoSocial.toLowerCase().includes(q.toLowerCase()) || c.cnpj.includes(q) || c.email.toLowerCase().includes(q.toLowerCase())
    )
    const start = p * s
    const content = filtered.slice(start, start + s)
    await route.fulfill({
      json: { content, totalElements: filtered.length, totalPages: Math.ceil(filtered.length / s), size: s, number: p, first: p === 0, last: (p + 1) * s >= filtered.length, empty: content.length === 0 }
    })
  })

  page.route(/\/v1\/clientes\/juridicos\/(\d+)\/permanent/, async (route) => {
    if (route.request().method() === 'DELETE') await route.fulfill({ status: 204 })
  })

  page.route(/\/v1\/clientes\/juridicos\/(\d+)\/ativar/, async (route) => {
    await route.fulfill({ status: 200 })
  })

  page.route(/\/v1\/clientes\/juridicos\/(\d+)\/inativar/, async (route) => {
    await route.fulfill({ status: 200 })
  })

  page.route(/\/v1\/clientes\/juridicos\/(\d+)$/, async (route) => {
    const match = route.request().url().match(/\/clientes\/juridicos\/(\d+)$/)
    if (match) {
      const id = parseInt(match[1])
      const c = mockClientesJuridicos.find(x => x.id === id)
      await route.fulfill({ status: c ? 200 : 404, json: c || { message: 'Not found' } })
    }
  })

  page.route(/\/v1\/clientes\/juridicos(\?|$)/, async (route, request) => {
    if (request.method() === 'POST') {
      const body = JSON.parse(request.postData() || '{}')
      const novo = { id: mockClientesJuridicos.length + 1, ...body, estaAtivo: true, dataCriacao: new Date().toISOString(), dataAtualizacao: new Date().toISOString() }
      delete novo.enderecos
      mockClientesJuridicos.push(novo)
      await route.fulfill({ status: 201, json: novo })
    } else if (request.method() === 'GET') {
      const url = new URL(request.url())
      const p = parseInt(url.searchParams.get('page') || '0')
      const s = parseInt(url.searchParams.get('size') || '10')
      const start = p * s
      const content = mockClientesJuridicos.slice(start, start + s)
      await route.fulfill({
        json: { content, totalElements: mockClientesJuridicos.length, totalPages: Math.ceil(mockClientesJuridicos.length / s), size: s, number: p, first: p === 0, last: (p + 1) * s >= mockClientesJuridicos.length, empty: content.length === 0 }
      })
    }
  })
}
