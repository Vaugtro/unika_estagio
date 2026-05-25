export * from './arquivo.service';
import {ArquivoService} from './arquivo.service';
import {ClientesFisicosService} from './clientesFisicos.service';
import {ClientesJuridicosService} from './clientesJuridicos.service';
import {EnderecosService} from './enderecos.service';

export * from './clientesFisicos.service';

export * from './clientesJuridicos.service';

export * from './enderecos.service';

export const APIS = [ArquivoService, ClientesFisicosService, ClientesJuridicosService, EnderecosService];
