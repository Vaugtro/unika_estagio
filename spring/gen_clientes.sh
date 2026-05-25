#!/bin/bash
# Gera e insere 25 clientes aleatorios (13 Fisicos + 12 Juridicos) com 1-5 enderecos cada
# Uso: bash gen_clientes.sh

python3 << 'PYEOF'
import random, json, subprocess, sys, time

random.seed()

def gen_cpf():
    cpf = [random.randint(0, 9) for _ in range(9)]
    s = sum((10-i)*cpf[i] for i in range(9))
    d1 = 0 if s % 11 < 2 else 11 - (s % 11)
    cpf.append(d1)
    s = sum((11-i)*cpf[i] for i in range(10))
    d2 = 0 if s % 11 < 2 else 11 - (s % 11)
    cpf.append(d2)
    return ''.join(str(d) for d in cpf)

def gen_cnpj():
    cnpj = [random.randint(0, 9) for _ in range(12)]
    w1 = [5,4,3,2,9,8,7,6,5,4,3,2]
    s = sum(w1[i]*cnpj[i] for i in range(12))
    d1 = 0 if s % 11 < 2 else 11 - (s % 11)
    cnpj.append(d1)
    w2 = [6,5,4,3,2,9,8,7,6,5,4,3,2]
    s = sum(w2[i]*cnpj[i] for i in range(13))
    d2 = 0 if s % 11 < 2 else 11 - (s % 11)
    cnpj.append(d2)
    return ''.join(str(d) for d in cnpj)

def gen_telefone():
    ddd = random.randint(1,9)*10 + random.randint(1,9)
    return f"({ddd}) 9{random.randint(1000,9999)}-{random.randint(1000,9999)}"

first_names = ["Joao", "Maria", "Pedro", "Ana", "Carlos", "Lucas", "Mariana", "Rafael", "Julia", "Fernando",
               "Beatriz", "Eduardo", "Larissa", "Thiago", "Camila", "Bruno", "Amanda", "Felipe", "Vanessa", "Roberto"]
last_names = ["Silva", "Santos", "Oliveira", "Souza", "Lima", "Pereira", "Costa", "Ferreira", "Rodrigues", "Almeida",
              "Nascimento", "Araujo", "Ribeiro", "Carvalho", "Gomes", "Martins", "Barbosa", "Rocha", "Dias", "Moreira"]
cities = ["Sao Paulo", "Rio de Janeiro", "Belo Horizonte", "Porto Alegre", "Curitiba", "Salvador", "Fortaleza", "Brasilia", "Recife", "Manaus"]
states = ["SP", "RJ", "MG", "RS", "PR", "BA", "CE", "DF", "PE", "AM"]
streets = ["Rua das Flores", "Avenida Paulista", "Rua Augusta", "Avenida Atlantica", "Rua XV de Novembro",
           "Rua da Praia", "Avenida Brasil", "Rua do Comercio", "Praca da Se", "Rua Direita"]
bairros = ["Centro", "Jardins", "Bela Vista", "Copacabana", "Boa Viagem", "Barra da Tijuca", "Savassi", "Moinhos de Vento", "Batel", "Aldeota"]

def gen_endereco(i, ddd=None):
    return {
        "logradouro": random.choice(streets),
        "numero": random.randint(1, 9999),
        "cep": f"{random.randint(10000, 99999)}-{random.randint(100, 999)}",
        "bairro": random.choice(bairros),
        "telefone": gen_telefone(),
        "estado": random.choice(states),
        "cidade": random.choice(cities),
        "principal": i == 0,
        "complemento": random.choice(["", "Apto " + str(random.randint(1, 100)), "Sala " + str(random.randint(1, 100))])
    }

def post(url, body, max_retries=3):
    for attempt in range(max_retries):
        resp = subprocess.run(
            ["curl", "-s", "-w", "\n%{http_code}", "-X", "POST", url,
             "-H", "Content-Type: application/json",
             "-d", json.dumps(body)],
            capture_output=True, text=True)
        raw = resp.stdout.strip()
        lines = raw.rsplit("\n", 1)
        code = lines[-1].strip()
        response_body = lines[0] if len(lines) > 1 else ""
        if code == "201":
            return code, response_body
        if code == "409" and attempt < max_retries - 1:
            doc_key = "cpf" if "cpf" in body else "cnpj"
            body[doc_key] = gen_cpf() if "cpf" in body else gen_cnpj()
            continue
        return code, response_body
    return code, response_body

results = {"success": 0, "fail": 0}
for idx in range(25):
    qtd = random.randint(1, 5)
    if idx < 13:
        nome = f"{random.choice(first_names)} {random.choice(last_names)} {random.choice(last_names)}"
        cpf = gen_cpf()
        email = nome.lower().replace(" ", ".") + "@email.com"
        rg = ''.join(str(random.randint(0,9)) for _ in range(random.randint(8,9)))
        data = f"{random.randint(1970,2002)}-{random.randint(1,12):02d}-{random.randint(1,28):02d}"
        enderecos = [gen_endereco(i) for i in range(qtd)]
        # remove clienteId if present (EnderecoWithinClienteCreateRequest has none)
        for e in enderecos:
            e.pop("clienteId", None)
        body = {"cpf": cpf, "nome": nome, "rg": rg, "email": email,
                "dataNascimento": data, "enderecos": enderecos}
        code, err = post("http://localhost:8080/v1/clientes/fisicos", body)
        status = "OK" if code == "201" else f"FAIL({code})"
        results["success" if code == "201" else "fail"] += 1
        if code != "201":
            print(f"[{idx+1}/25] FISICO {nome} ({cpf}) -> {status} | {err[:200]}")
        else:
            print(f"[{idx+1}/25] FISICO {nome} ({cpf}) | {qtd} endereco(s) -> {status}")
    else:
        razao = f"{random.choice(last_names).upper()} {random.choice(['LTDA', 'S.A.', 'ME', 'EIRELI'])}"
        cnpj = gen_cnpj()
        email = "contato@" + razao.lower().split()[0] + ".com.br"
        ie = f"{random.randint(100000000, 999999999)}"
        data = f"{random.randint(2000,2022)}-{random.randint(1,12):02d}-{random.randint(1,28):02d}"
        enderecos = [gen_endereco(i) for i in range(qtd)]
        for e in enderecos:
            e["clienteId"] = 0  # service replaces with real ID
        body = {"cnpj": cnpj, "razaoSocial": razao, "inscricaoEstadual": ie,
                "email": email, "dataCriacaoEmpresa": data, "enderecos": enderecos}
        code, err = post("http://localhost:8080/v1/clientes/juridicos", body)
        status = "OK" if code == "201" else f"FAIL({code})"
        results["success" if code == "201" else "fail"] += 1
        if code != "201":
            print(f"[{idx+1}/25] JURIDICO {razao} -> {status} | {err[:200]}")
        else:
            print(f"[{idx+1}/25] JURIDICO {razao} ({cnpj}) | {qtd} endereco(s) -> {status}")

print(f"\nDone: {results['success']} success, {results['fail']} failed")
PYEOF
