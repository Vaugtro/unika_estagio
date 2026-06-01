import { Page } from '@playwright/test'
import { isRealApi } from '../helpers/test-mode'

function generateEnderecos(count: number) {
  const enderecos: any[] = []
  for (let i = 0; i < count; i++) {
    enderecos.push({
      id: i + 1,
      logradouro: `Rua ${['das Flores', 'dos Pinheiros', 'Santos', 'Augusta', 'da Consolação'][i % 5]}`,
      numero: String(100 + i * 10),
      bairro: ['Centro', 'Bela Vista', 'Jardins', 'Pinheiros', 'Vila Nova'][i % 5],
      cidade: 'São Paulo',
      estado: 'SP',
      cep: `0100${String(i + 1).padStart(3, '0')}-000`,
      telefone: `(11) 99999-${String(1000 + i).padStart(4, '0')}`,
      complemento: i === 0 ? '' : `Apto ${i}`,
      principal: i === 0,
    })
  }
  return enderecos
}

const INITIAL_COUNT = 3
export let mockEnderecos = generateEnderecos(INITIAL_COUNT)
let nextId = INITIAL_COUNT + 1

export function resetEnderecos() {
  mockEnderecos.length = 0
  mockEnderecos.push(...generateEnderecos(INITIAL_COUNT))
  nextId = INITIAL_COUNT + 1
}

function toPage(data: any[]) {
  return {
    content: data,
    totalElements: data.length,
    totalPages: Math.ceil(data.length / 10),
    size: 10,
    number: 0,
    sort: { sorted: false, unsorted: true, empty: true },
    numberOfElements: data.length,
    pageable: { pageNumber: 0, pageSize: 10, sort: { sorted: false, unsorted: true, empty: true }, offset: 0, paged: true, unpaged: false },
    first: true,
    last: true,
    empty: data.length === 0
  }
}

export function setupEnderecosMocks(page: Page) {
  if (isRealApi()) return
  page.route(/\/v1\/enderecos\/clientes\//, async (route) => {
    const url = route.request().url()
    if (url.includes('/has-principal')) {
      await route.fulfill({ json: mockEnderecos.some(e => e.principal) })
    } else if (url.includes('/has-addresses')) {
      await route.fulfill({ json: mockEnderecos.length > 0 })
    } else if (url.includes('/count')) {
      await route.fulfill({ json: mockEnderecos.length })
    } else {
      await route.fulfill({ json: toPage(mockEnderecos) })
    }
  })

  page.route(/\/v1\/enderecos(\?|$)/, async (route, request) => {
    if (request.method() === 'POST') {
      const body = JSON.parse(request.postData() || '{}')
      const novo = { id: nextId++, logradouro: body.logradouro, numero: body.numero, bairro: body.bairro, cep: body.cep, telefone: body.telefone || '', complemento: body.complemento || '', principal: body.principal ?? false, cidade: 'São Paulo', estado: 'SP' }
      mockEnderecos.push(novo)
      await route.fulfill({ status: 201, json: novo })
    }
  })

  page.route(/\/v1\/enderecos\/search/, async (route) => {
    await route.fulfill({ json: toPage(mockEnderecos) })
  })

  page.route(/\/v1\/enderecos\/(\d+)\/principal/, async (route) => {
    const match = route.request().url().match(/\/enderecos\/(\d+)\/principal/)
    if (match) {
      const id = parseInt(match[1])
      mockEnderecos.forEach(e => { e.principal = e.id === id })
      await route.fulfill({ status: 200 })
    }
  })

  page.route(/\/v1\/enderecos\/(\d+)$/, async (route) => {
    const match = route.request().url().match(/\/enderecos\/(\d+)$/)
    if (!match) return
    const id = parseInt(match[1])
    switch (route.request().method()) {
      case 'PUT': {
        const body = JSON.parse(route.request().postData() || '{}')
        const idx = mockEnderecos.findIndex(e => e.id === id)
        if (idx >= 0) {
          mockEnderecos[idx] = { ...mockEnderecos[idx], ...body }
          await route.fulfill({ json: mockEnderecos[idx] })
        } else {
          await route.fulfill({ status: 404 })
        }
        break
      }
      case 'DELETE': {
        const idx = mockEnderecos.findIndex(e => e.id === id)
        if (idx < 0) {
          await route.fulfill({ status: 404 })
          break
        }
        const wasPrincipal = mockEnderecos[idx].principal
        const isOnlyOne = mockEnderecos.length === 1
        if (wasPrincipal && isOnlyOne) {
          await route.fulfill({ status: 400, json: { message: 'Não é possível excluir o único endereço principal' } })
          break
        }
        mockEnderecos.splice(idx, 1)
        if (wasPrincipal && mockEnderecos.length > 0) {
          mockEnderecos[0].principal = true
        }
        await route.fulfill({ status: 204 })
        break
      }
    }
  })
}
