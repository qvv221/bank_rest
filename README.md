<h1>Системы Управления Банковскими Картами</h1>

<h2>Запуск</h2>

<pre><code>docker compose up -d</code></pre>

<h3>Swagger UI и OpenAPI</h3>
<ul>
  <li>Swagger UI: <code>http://localhost:8080/swagger-ui/index.html</code></li>
</ul>

<h3>Как авторизоваться (JWT) в Swagger UI</h3>
<ol>
  <li>Открой Swagger UI.</li>
  <li>Вызови <code>POST /auth/sign-in</code> (например, пользователь <code>admin</code> из миграции) и скопируй поле <code>token</code>.</li>
  <li>Нажми кнопку <strong>Authorize</strong> и вставь: <code>Bearer &lt;token&gt;</code>.</li>
  <li>Теперь можно вызывать защищённые эндпоинты.</li>
</ol>

<h4>Пользователи с паролями</h4>
<ul>
  <li><code>admin</code> / <code>admin</code> — ADMIN</li>
  <li><code>user1</code> / <code>password</code> — USER</li>
  <li><code>user2</code> / <code>password</code> — USER</li>
  <li><code>user3</code> / <code>password</code> — USER</li>
  <li><code>user4</code> / <code>password</code> — USER</li>
  <li><code>user5</code> / <code>password</code> — USER</li>
</ul>

<h4>Карты</h4>
<ul>
  <li><code>1</code> / <code>410000000001</code> — <code>user1</code></li>
  <li><code>2</code> / <code>410000000002</code> — <code>user2</code></li>
  <li><code>3</code> / <code>410000000003</code> — <code>user2</code></li>
  <li><code>4</code> / <code>410000000004</code> — <code>user3</code></li>
  <li><code>5</code> / <code>410000000005</code> — <code>user4</code></li>
  <li><code>6</code> / <code>410000000006</code> — <code>user5</code></li>
  <li><code>7</code> / <code>410000000007</code> — <code>user5</code></li>
  <li><code>8</code> / <code>410000000008</code> — <code>user2</code></li>
  <li><code>9</code> / <code>410000000009</code> — <code>user2</code></li>
  <li><code>10</code> / <code>410000000010</code> — <code>user4</code></li>
</ul>
