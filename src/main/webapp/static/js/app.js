(function () {
    const config = window.appConfig || {};
    let results = normalize(config.results);

    let board;
    let areaElements = [];
    let plottedPoints = [];
    let selectedRadius = null;

    document.addEventListener('DOMContentLoaded', () => {
        board = initBoard();
        selectedRadius = getSelectedRadius() || 1;
        updateHiddenRadiusField();
        highlightRadius();
        drawArea();
        updateCurrentTime();
        setInterval(updateCurrentTime, 1000);

        document.addEventListener('submit', handleFormSubmit, true);
        document.addEventListener('input', handleYInput, true);

        window.setClientRadius = function (value, element) {
            selectedRadius = parseFloat(value);
            updateHiddenRadiusField();
            highlightRadius(element);
            drawArea();
            return true;
        };

        window.updateResultsFromServer = data => {
            results = normalize(data);
            drawResultsOnBoard();
            highlightRadius();
        };

        window.updateHiddenRadiusFromDom = () => {
            selectedRadius = getSelectedRadius() || selectedRadius;
            updateHiddenRadiusField();
            drawArea();
            highlightRadius();
        };

        window.setRadiusFromServer = function (value) {
            const parsed = parseFloat(value);
            if (!isNaN(parsed)) {
                selectedRadius = parsed;
                updateHiddenRadiusField();
                drawArea();
                highlightRadius();
            }
        };

        if (window.jsf && window.jsf.ajax && typeof window.jsf.ajax.addOnEvent === 'function') {
            window.jsf.ajax.addOnEvent(event => {
                if (event && event.status === 'success') {
                    window.updateHiddenRadiusFromDom();
                }
            });
        }
    });

    function updateHiddenRadiusField() {
        const hidden = document.getElementById('graph-r');
        if (hidden && selectedRadius !== null && !isNaN(selectedRadius)) {
            hidden.value = selectedRadius;
        }
        const links = document.getElementById('radius-links');
        if (links && selectedRadius !== null && !isNaN(selectedRadius)) {
            links.dataset.selectedR = selectedRadius;
        }
    }

    function normalize(data) {
        if (Array.isArray(data)) return data;
        if (typeof data === 'string') {
            try {
                const parsed = JSON.parse(data);
                return Array.isArray(parsed) ? parsed : [];
            } catch {
                return [];
            }
        }
        return [];
    }

    function initBoard() {
        const newBoard = JXG.JSXGraph.initBoard('graph-board', {
            boundingbox: [-6, 6, 6, -6],
            axis: true,
            keepaspectratio: true,
            showNavigation: false,
            showCopyright: false
        });
        newBoard.on('down', handleBoardClick);
        return newBoard;
    }

    function getSelectedRadius() {
        const hidden = document.getElementById('graph-r');
        if (hidden) {
            const hiddenVal = parseFloat(hidden.value);
            if (!isNaN(hiddenVal)) {
                selectedRadius = hiddenVal;
                return hiddenVal;
            }
        }
        const links = document.getElementById('radius-links');
        if (links) {
            const value = parseFloat(links.dataset.selectedR);
            if (!isNaN(value)) {
                selectedRadius = value;
                return value;
            }
        }
        return selectedRadius !== null && !isNaN(selectedRadius) ? selectedRadius : null;
    }

    function drawArea() {
        const radius = selectedRadius || getSelectedRadius() || 1;
        if (!board) return;

        areaElements.forEach(el => board.removeObject(el));
        areaElements = [];

        const point = coords => board.create('point', coords, { visible: false, fixed: true });

        const rectPoints = [
            point([0, 0]),
            point([-radius, 0]),
            point([-radius, radius]),
            point([0, radius])
        ];
        const rectangle = board.create('polygon', rectPoints, {
            withLines: false,
            fillColor: '#3498db',
            fillOpacity: 0.35,
            borders: { strokeWidth: 0 }
        });

        const trianglePoints = [
            point([0, 0]),
            point([radius / 2, 0]),
            point([0, radius])
        ];
        const triangle = board.create('polygon', trianglePoints, {
            withLines: false,
            fillColor: '#3498db',
            fillOpacity: 0.35,
            borders: { strokeWidth: 0 }
        });

        const center = point([0, 0]);
        const arcStart = point([0, -radius / 2]);
        const arcEnd = point([radius / 2, 0]);
        const sector = board.create('sector', [center, arcStart, arcEnd], {
            withLines: false,
            fillColor: '#3498db',
            fillOpacity: 0.35,
            strokeColor: '#3498db'
        });

        areaElements = [...rectPoints, rectangle, ...trianglePoints, triangle, center, arcStart, arcEnd, sector];
        drawResultsOnBoard();
    }

    function drawResultsOnBoard() {
        if (!board) return;

        plottedPoints.forEach(p => board.removeObject(p));
        plottedPoints = [];

        const radius = getSelectedRadius();
        if (radius === null || !Array.isArray(results)) return;

        results.forEach(result => {
            const x = parseFloat(result.x);
            const y = parseFloat(result.y);
            const r = parseFloat(result.r);
            if ([x, y, r].some(v => isNaN(v))) return;
            if (Math.abs(r - radius) > 1e-9) return;

            const point = board.create('point', [x, y], {
                withLabel: false,
                size: 4,
                fixed: true,
                strokeColor: result.result ? '#27ae60' : '#e74c3c',
                fillColor: result.result ? '#27ae60' : '#e74c3c'
            });
            plottedPoints.push(point);
        });
    }

    function handleFormSubmit(event) {
        if (!event.target || event.target.id !== 'point-form') return;
        const xField = document.getElementById('point-form:x-value');
        const yField = document.getElementById('point-form:y-value');
        const radius = getSelectedRadius();

        if (!validateForm(xField?.value, yField?.value, radius)) {
            event.preventDefault();
        } else {
            hideError();
        }
    }

    function handleBoardClick(event) {
        const radius = getSelectedRadius();
        if (!radius) {
            showError('Сначала выберите радиус R, чтобы определить координаты точки.');
            return;
        }
        hideError();
        const coords = board.getUsrCoordsOfMouse(event);
        if (!coords || coords.length < 2) return;
        const boundedX = clamp(coords[0], -6, 6);
        const boundedY = clamp(coords[1], -6, 6);

        const hiddenX = document.getElementById('graph-x');
        const hiddenY = document.getElementById('graph-y');
        const hiddenR = document.getElementById('graph-r');
        if (!hiddenX || !hiddenY || !hiddenR) return;

        hiddenX.value = boundedX.toFixed(3);
        hiddenY.value = boundedY.toFixed(3);
        hiddenR.value = radius;

        const button = document.getElementById(config.graphFormSubmitId);
        button && button.click();
    }

    function validateForm(x, y, radius) {
        if (!x && x !== 0) {
            showError('Выберите координату X.');
            return false;
        }
        const normalized = (y || '').replace(',', '.');
        const yNum = parseFloat(normalized);
        const invalidY = isNaN(yNum) || yNum < -5 || yNum > 3;
        setYWarningVisible(invalidY);
        if (invalidY) {
            showError('Координата Y должна быть числом в диапазоне от -5 до 3.');
            return false;
        }
        if (radius === null) {
            showError('Выберите радиус R.');
            return false;
        }
        return true;
    }

    function showError(message) {
        const errorDiv = document.getElementById('error-message');
        if (errorDiv) {
            errorDiv.textContent = message;
            errorDiv.style.display = 'block';
        }
    }

    function hideError() {
        const errorDiv = document.getElementById('error-message');
        if (errorDiv) {
            errorDiv.style.display = 'none';
        }
    }

    function updateCurrentTime() {
        const timeElement = document.getElementById('current-time');
        if (timeElement) {
            timeElement.textContent = 'Текущее время: ' + new Date().toLocaleString();
        }
    }

    function handleYInput(event) {
        if (!event.target || event.target.id !== 'point-form:y-value') return;
        const value = event.target.value.replace(',', '.');
        if (value && !isNaN(value)) {
            const num = parseFloat(value);
            const invalid = num < -5 || num > 3;
            event.target.style.borderColor = invalid ? '#e74c3c' : '#bdc3c7';
            setYWarningVisible(invalid);
        } else {
            event.target.style.borderColor = '#bdc3c7';
            setYWarningVisible(!!value);
        }
    }

    function highlightRadius(element) {
        const links = document.querySelectorAll('#radius-links .radius-link');
        links.forEach(link => {
            link.classList.remove('client-active');
            link.classList.remove('active');
        });

        if (element) {
            element.classList.add('client-active');
            return;
        }

        const current = selectedRadius;
        links.forEach(link => {
            const value = parseFloat(link.getAttribute('data-radius'));
            if (!isNaN(value) && current !== null && Math.abs(value - current) < 1e-9) {
                link.classList.add('client-active');
            }
        });
    }

    window.redrawHitArea = function () {
        drawArea();
    };

    function clamp(value, min, max) {
        return Math.max(min, Math.min(max, value));
    }

    function setYWarningVisible(visible) {
        const warning = document.getElementById('y-warning');
        if (warning) {
            warning.style.display = visible ? 'block' : 'none';
        }
    }
})();
