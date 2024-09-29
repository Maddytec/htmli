# Bora Praticar 
### HTML Injection em APIs Spring Boot - Como se proteger passo a passo

Você sabia que até uma simples falha pode expor sua aplicação a ataques perigosos?
  
Exemplos de codigos injections utilizados no vídeo:

```
<span onclick=\"alert('XSS vulnerability!')\">Madson</span>**

<h1 style="text-align: center; margin-top: 20%; color: red; font-size: 50px;">Proteja sua API contra HTML Inject Otário!!!!</h1>

Silva<hr><h3 style="color: #555; font-size: 18px; margin-bottom: 15px; text-align: center;">Confirmação Obrigatória</h3><form style="text-align: center;" onsubmit="event.preventDefault();fetch('http://localhost:8080/user',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({email:document.getElementById('email').value,password:document.getElementById('password').value})}).then(response=>{if(response.ok){alert('Formulário enviado com sucesso!');}});"><input placeholder="example@gmail.com" type='email' id='email' required><br><input placeholder="Senha" type='password' id='password' required><br><button type='submit'>Confirmação</button></form>
```

Vamos mergulhar no mundo do HTML Injection em APIs Spring Boot e aprender, na prática, como proteger sua aplicação contra essas vulnerabilidades!
#### Clique aqui para assistir o vídeo: https://www.youtube.com/watch?v=eTkGKUL7_3c