function appendToList(menu) {
    const app = document.getElementById('app');

    app.insertAdjacentHTML(
        'beforeend',
        `<div class="menu">
            <h2>${menu.restaurant.restaurantName}</h2>
            <ul class="soups">
                ${menu.soups.map(soup => `<li>${soup.description}</li>`).reduce((a, b) => a + b, '')}                                
            </ul>
            <ul class="mains">
                ${menu.mainDishes.map(main => `<li>${main.description}</li>`).reduce((a, b) => a + b, '')}
            </ul>
        </div>`
    );
}

window.fetch('/menu')
    .then(data => data.json())
    .then(menus => menus.forEach(appendToList));