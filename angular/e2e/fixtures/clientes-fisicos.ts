import { Page } from '@playwright/test'

export const mockClientesFisicos: any[] = [
  { id: 1, nome: 'João Silva', cpf: '123.456.789-00', email: 'joao@email.com', rg: '123456789', dataNascimento: '1990-01-01', estaAtivo: true, dataCriacao: '2024-01-01T00:00:00', dataAtualizacao: '2024-06-01T00:00:00' },
  { id: 2, nome: 'Maria Souza', cpf: '987.654.321-00', email: 'maria@email.com', rg: '987654321', dataNascimento: '1985-05-15', estaAtivo: true, dataCriacao: '2024-02-01T00:00:00', dataAtualizacao: '2024-06-01T00:00:00' },
  { id: 3, nome: 'Carlos Inativo', cpf: '111.222.333-44', email: 'carlos@email.com', rg: '111222333', dataNascimento: '1975-03-20', estaAtivo: false, dataCriacao: '2024-03-01T00:00:00', dataAtualizacao: '2024-06-01T00:00:00' },
]

export function setupClientesFisicosMocks(page: Page) {
  page.route('**/viacep.com.br/**', (route) => route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ erro: true }) }))

  page.route('**/v1/unidades-federativas', (route) =>
    route.fulfill({ json: [{ sigla: 'SP', nome: 'São Paulo' }, { sigla: 'RJ', nome: 'Rio de Janeiro' }] }))

  page.route('**/v1/municipios**', (route) =>
    route.fulfill({ json: [{ id: 3550308, nome: 'São Paulo' }, { id: 3509502, nome: 'Campinas' }] }))

  page.route(/\/v1\/clientes\/fisicos\/search(\?|$)/, async (route) => {
    const url = new URL(route.request().url())
    const q = url.searchParams.get('q') || ''
    const p = parseInt(url.searchParams.get('page') || '0')
    const s = parseInt(url.searchParams.get('size') || '10')
    const filtered = mockClientesFisicos.filter(c =>
      c.nome.toLowerCase().includes(q.toLowerCase()) || c.cpf.includes(q) || c.email.toLowerCase().includes(q.toLowerCase())
    )
    const start = p * s
    const content = filtered.slice(start, start + s)
    await route.fulfill({
      json: { content, totalElements: filtered.length, totalPages: Math.ceil(filtered.length / s), size: s, number: p, first: p === 0, last: (p + 1) * s >= filtered.length, empty: content.length === 0 }
    })
  })

  page.route(/\/v1\/clientes\/fisicos\/(\d+)\/permanent/, async (route) => {
    if (route.request().method() === 'DELETE') await route.fulfill({ status: 204 })
  })

  page.route(/\/v1\/clientes\/fisicos\/(\d+)\/ativar/, async (route) => {
    await route.fulfill({ status: 200 })
  })

  page.route(/\/v1\/clientes\/fisicos\/(\d+)\/inativar/, async (route) => {
    await route.fulfill({ status: 200 })
  })

  page.route(/\/v1\/clientes\/fisicos\/(\d+)$/, async (route) => {
    const match = route.request().url().match(/\/clientes\/fisicos\/(\d+)$/)
    if (match) {
      const id = parseInt(match[1])
      const c = mockClientesFisicos.find(x => x.id === id)
      await route.fulfill({ status: c ? 200 : 404, json: c || { message: 'Not found' } })
    }
  })

  page.route(/\/v1\/clientes\/fisicos(\?|$)/, async (route, request) => {
    if (request.method() === 'POST') {
      const body = JSON.parse(request.postData() || '{}')
      const novo = { id: mockClientesFisicos.length + 1, ...body, estaAtivo: true, dataCriacao: new Date().toISOString(), dataAtualizacao: new Date().toISOString() }
      delete novo.enderecos
      mockClientesFisicos.push(novo)
      await route.fulfill({ status: 201, json: novo })
    } else if (request.method() === 'GET') {
      const url = new URL(request.url())
      const p = parseInt(url.searchParams.get('page') || '0')
      const s = parseInt(url.searchParams.get('size') || '10')
      const start = p * s
      const content = mockClientesFisicos.slice(start, start + s)
      await route.fulfill({
        json: { content, totalElements: mockClientesFisicos.length, totalPages: Math.ceil(mockClientesFisicos.length / s), size: s, number: p, first: p === 0, last: (p + 1) * s >= mockClientesFisicos.length, empty: content.length === 0 }
      })
    }
  })
}
