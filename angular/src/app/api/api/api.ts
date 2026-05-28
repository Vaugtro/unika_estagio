export * from './arquivo.service';
import { ArquivoService } from './arquivo.service';
export * from './clientesFisicos.service';
import { ClientesFisicosService } from './clientesFisicos.service';
export * from './clientesJuridicos.service';
import { ClientesJuridicosService } from './clientesJuridicos.service';
export * from './enderecos.service';
import { EnderecosService } from './enderecos.service';
export const APIS = [ArquivoService, ClientesFisicosService, ClientesJuridicosService, EnderecosService];
