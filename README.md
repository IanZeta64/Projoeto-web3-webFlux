# Projeto-web3-webFlux*


Projeto final | Santander 938 | WEBIII

Implementar uma API REST de qualquer assunto utilizando a arquitetura de microsserviços. A aplicação deve conter pelo menos 3 microsserviços. A implementação das APIs deve utilizar o Spring WebFlux e deve utilizar pelo menos 1 ferramenta do Spring Cloud visto em aula.

A entrega deve ser feita através do GITHUB enviando o link do repositório aqui. Lembre-se de deixar o repositório público.

> Microservicos: 
-jogos: criar, atualizar, buscar, deletar e alterar placar e status do jogo.
-usuario: criar, atualizar, deletar, buscar e alterar saldo de usuario.
-aposta: criar e buscar aposta baseado nos jogos e usuario.
 
> Funcionalidade de integracao dos microsservicos: 
-registrar aposta para usuarios existentes e com saldo maior que o valor apostado.
-registrar aposta baseado em jogos existente e ativos
-atualizacao periodica de apostas de jogos ainda nao iniciados ou encerrados.
-atualizacao periodica de apostas encerradas que estao com premiacao disponivel e deposito automatico para o usuario.

> Funcionalidades Cloud:
-eureka: registro de server pelo eureka
-gateway: mascara de para os microservicos
-circuitbreaker: fallback para metodos do client

>Principais ferramentas e dependencias:
-Spring webflux
-Mongodb reativo
-spring cloud eureka server
-spring cloud gateway
-spring circuit breaker
-lombok
-dev-tools

