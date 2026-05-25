export * from './clientesFisicos.service';
import { ClientesFisicosService } from './clientesFisicos.service';
export * from './clientesJuridicos.service';
import { ClientesJuridicosService } from './clientesJuridicos.service';
export * from './enderecos.service';
import { EnderecosService } from './enderecos.service';
export * from './exportacao.service';
import { ExportacaoService } from './exportacao.service';
export const APIS = [ClientesFisicosService, ClientesJuridicosService, EnderecosService, ExportacaoService];
