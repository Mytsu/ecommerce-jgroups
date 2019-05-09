# Trabalho de Sistemas Distribuídos
##### Instituto Federal de Minas Gerais - Campus Formiga
##### Jonathan Arantes

___

## Objetivo

Desenvolvimento de um _Sistema Distribuído_ (SD) que provê um serviço de _e-marketplace_. A estrutura do serviço deverá ser provida pelo [middleware JGroups](jgroups.org), e a comunicação entre componentes -- membros do(s) cluster(s) -- realizada através de conectores [JChannel](http://jgroups.org/manual/index.html#JChannel), do componente [MessageDispatcher](jgroups.org/manual/index.html#MessageDispatcher) (_multicast_, _anycast_, _unicast_), demais _building blocks_ e protocolos que se fizerem necessários.

### Funcionalidades

O serviço deve permitir o __anúncio__ de produtos (dos vendedores) para serem comercializados (aos consumidores) em uma loja virtual.

Ao se cadastrar, o consumidor iniciará com um valor inicial de créditos (ex: 1000). Ao se cadastrar, o vendedor irá iniciar com 0 créditos na loja virtual, após o cadastro, o vendedor poderá anunciar produtos livremente.

Produtos iguais poderão ser anunciados por vendedores diferentes em quantidades diferentes, desta forma, é necessário um __controle do estoque de produtos__.

Consumidores poderão __realizar pesquisas pelos produtos anunciados__, podendo ordenar os resultados (ex: menor preço, anúncio mais recente, etc). Este poderá __efetuar a compra__ de um produto, transferindo os créditos para o vendedor deste produto. Este também poderá __acessar seu histórico de compras__.

Vendedores poderão __verificar seu histórico de vendas__ e __consultar seu saldo__.