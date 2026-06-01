import { APIRequestContext } from '@playwright/test'

export const PF_CLIENTS = [
  { nome: 'João Silva', cpf: '123.456.789-00', email: 'joao@email.com', rg: '123456789', dataNascimento: '1990-01-01' },
  { nome: 'Maria Souza', cpf: '987.654.321-00', email: 'maria@email.com', rg: '987654321', dataNascimento: '1985-05-15' },
  { nome: 'Carlos Inativo', cpf: '111.222.333-44', email: 'carlos@email.com', rg: '111222333', dataNascimento: '1975-03-20' },
]

export const PJ_CLIENTS = [
  { cnpj: '11.222.333/0001-44', razaoSocial: 'Empresa Alpha Ltda', nomeFantasia: 'Alpha', inscricaoEstadual: '123456789', email: 'alpha@empresa.com', dataCriacaoEmpresa: '2000-01-01' },
  { cnpj: '55.666.777/0001-88', razaoSocial: 'Beta Comércio S.A.', nomeFantasia: 'Beta', inscricaoEstadual: '987654321', email: 'beta@comercio.com', dataCriacaoEmpresa: '2010-06-15' },
  { cnpj: '99.888.777/0001-66', razaoSocial: 'Gamma Inativa ME', nomeFantasia: 'Gamma', inscricaoEstadual: '456789123', email: 'gamma@inativa.com', dataCriacaoEmpresa: '2020-03-20' },
]

function makeEndereco(principal: boolean) {
  return { logradouro: 'Rua das Flores', numero: 100, bairro: 'Centro', cep: '01001-000', municipioId: 3550308, principal }
}

let cachedPfIds: number[] | null = null
let cachedPjIds: number[] | null = null

async function createOrGetPf(request: APIRequestContext, c: typeof PF_CLIENTS[0]): Promise<number | null> {
  const r = await request.post('/v1/clientes/fisicos', { data: { ...c, enderecos: [makeEndereco(true)] } })
  if (r.ok()) { const b = await r.json(); return b.id }
  const search = await request.get(`/v1/clientes/fisicos/search?q=${encodeURIComponent(c.cpf)}`)
  if (search.ok()) { const data = await search.json(); if (data.content?.length > 0) return data.content[0].id }
  console.error('PF create failed:', await r.text()); return null
}

async function createOrGetPj(request: APIRequestContext, c: typeof PJ_CLIENTS[0]): Promise<number | null> {
  const r = await request.post('/v1/clientes/juridicos', { data: { ...c, enderecos: [makeEndereco(true)] } })
  if (r.ok()) { const b = await r.json(); return b.id }
  const search = await request.get(`/v1/clientes/juridicos/search?q=${encodeURIComponent(c.cnpj)}`)
  if (search.ok()) { const data = await search.json(); if (data.content?.length > 0) return data.content[0].id }
  console.error('PJ create failed:', await r.text()); return null
}

export async function setupRealApiData(request: APIRequestContext) {
  if (cachedPfIds && cachedPjIds) return { pfIds: cachedPfIds, pjIds: cachedPjIds }

  const pfIds: number[] = []
  for (const c of PF_CLIENTS) {
    const id = await createOrGetPf(request, c)
    if (id) pfIds.push(id)
  }
  const pjIds: number[] = []
  for (const c of PJ_CLIENTS) {
    const id = await createOrGetPj(request, c)
    if (id) pjIds.push(id)
  }
  cachedPfIds = pfIds
  cachedPjIds = pjIds
  return { pfIds, pjIds }
}

export async function cleanupRealApiData(request: APIRequestContext, pfIds: number[], pjIds: number[]) {
  for (const id of pfIds) {
    await request.delete(`/v1/clientes/fisicos/${id}/permanent`).catch(() => {})
  }
  for (const id of pjIds) {
    await request.delete(`/v1/clientes/juridicos/${id}/permanent`).catch(() => {})
  }
}
