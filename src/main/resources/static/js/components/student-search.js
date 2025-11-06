const template = document.createElement('template');

template.innerHTML = `
  <style>
    :host {
      --student-search-font-family: inherit;
      --student-search-label-color: #0f172a;
      --student-search-label-size: 0.75rem;
      --student-search-input-bg: #ffffff;
      --student-search-input-color: #0f172a;
      --student-search-input-border: #cbd5f5;
      --student-search-input-radius: 0.75rem;
      --student-search-input-padding: 0.75rem 1rem;
      --student-search-input-shadow: inset 0 1px 2px rgba(15, 23, 42, 0.08);
      --student-search-input-focus-border: #6366f1;
      --student-search-input-focus-shadow: 0 0 0 4px rgba(99, 102, 241, 0.12);
      --student-search-clear-color: #94a3b8;
      --student-search-clear-hover-color: #475569;
      --student-search-dropdown-bg: #ffffff;
      --student-search-dropdown-border: rgba(148, 163, 184, 0.35);
      --student-search-dropdown-radius: 0.75rem;
      --student-search-dropdown-shadow: 0 18px 28px rgba(15, 23, 42, 0.15);
      --student-search-dropdown-max-height: 18rem;
      --student-search-dropdown-z-index: 1200;
      --student-search-option-color: #0f172a;
      --student-search-option-subtle: #64748b;
      --student-search-option-bg-hover: rgba(79, 70, 229, 0.08);
      --student-search-option-bg-active: rgba(79, 70, 229, 0.14);
      --student-search-message-color: #64748b;
      --student-search-helper-color: #64748b;
      --student-search-error-color: #dc2626;
      --student-search-spinner-color: #6366f1;
      --student-search-spacing: 0.5rem;
      --student-search-dropdown-offset: 0.35rem;
      display: block;
      font-family: var(--student-search-font-family);
    }

    .student-search {
      display: grid;
      gap: var(--student-search-spacing);
    }

    .student-search__label {
      font-size: var(--student-search-label-size);
      font-weight: 600;
      letter-spacing: 0.02em;
      color: var(--student-search-label-color);
      text-transform: uppercase;
    }

    .student-search__control {
      position: relative;
    }

    .student-search__input-wrapper {
      position: relative;
      display: flex;
      align-items: center;
    }

    .student-search__input {
      width: 100%;
      border: 1px solid var(--student-search-input-border);
      border-radius: var(--student-search-input-radius);
      padding: var(--student-search-input-padding);
      background-color: var(--student-search-input-bg);
      color: var(--student-search-input-color);
      font-size: 1rem;
      line-height: 1.5rem;
      box-shadow: var(--student-search-input-shadow);
      transition: border-color 150ms ease, box-shadow 150ms ease;
    }

    .student-search__input:focus {
      outline: none;
      border-color: var(--student-search-input-focus-border);
      box-shadow: var(--student-search-input-focus-shadow);
    }

    .student-search__input:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .student-search__clear {
      position: absolute;
      right: 0.75rem;
      background: none;
      border: none;
      color: var(--student-search-clear-color);
      font-size: 1.25rem;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0.25rem;
      border-radius: 999px;
      transition: color 120ms ease, background 120ms ease;
    }

    .student-search__clear:hover {
      color: var(--student-search-clear-hover-color);
      background-color: rgba(148, 163, 184, 0.12);
    }

    .student-search__clear:focus-visible {
      outline: 2px solid var(--student-search-input-focus-border);
      outline-offset: 2px;
    }

    .student-search__clear[hidden] {
      display: none;
    }

    .student-search__dropdown {
      position: absolute;
      inset: auto 0 auto 0;
      top: calc(100% + var(--student-search-dropdown-offset));
      background-color: var(--student-search-dropdown-bg);
      border: 1px solid var(--student-search-dropdown-border);
      border-radius: var(--student-search-dropdown-radius);
      box-shadow: var(--student-search-dropdown-shadow);
      max-height: var(--student-search-dropdown-max-height);
      overflow-y: auto;
      padding: 0.35rem;
      display: none;
      z-index: var(--student-search-dropdown-z-index);
    }

    .student-search__dropdown--open {
      display: block;
      animation: student-search-dropdown-in 120ms ease;
      transform-origin: top center;
    }

    @keyframes student-search-dropdown-in {
      from {
        opacity: 0;
        transform: translateY(-4px) scaleY(0.96);
      }
      to {
        opacity: 1;
        transform: translateY(0) scaleY(1);
      }
    }

    .student-search__message {
      padding: 0.75rem;
      color: var(--student-search-message-color);
      font-size: 0.875rem;
      text-align: center;
    }

    .student-search__message--error {
      color: var(--student-search-error-color);
    }

    .student-search__results {
      display: grid;
      gap: 0.25rem;
    }

    .student-search__option {
      display: grid;
      width: 100%;
      gap: 0.15rem;
      text-align: left;
      padding: 0.75rem 0.85rem;
      border-radius: 0.65rem;
      border: none;
      background: transparent;
      cursor: pointer;
      transition: background 120ms ease, transform 120ms ease;
      color: var(--student-search-option-color);
      font-size: 0.95rem;
      line-height: 1.35rem;
    }

    .student-search__option:hover {
      background-color: var(--student-search-option-bg-hover);
    }

    .student-search__option[aria-selected="true"],
    .student-search__option--active {
      background-color: var(--student-search-option-bg-active);
    }

    .student-search__option-primary {
      font-weight: 600;
    }

    .student-search__option-secondary {
      color: var(--student-search-option-subtle);
      font-size: 0.82rem;
    }

    .student-search__spinner {
      width: 1.5rem;
      height: 1.5rem;
      border-radius: 999px;
      border: 3px solid rgba(99, 102, 241, 0.25);
      border-top-color: var(--student-search-spinner-color);
      animation: student-search-spin 600ms linear infinite;
      margin: 0 auto;
    }

    @keyframes student-search-spin {
      to { transform: rotate(360deg); }
    }

    .student-search__helper {
      font-size: 0.78rem;
      color: var(--student-search-helper-color);
      margin: 0;
    }

    .student-search__helper[hidden] {
      display: none;
    }

    ::selection {
      background: rgba(79, 70, 229, 0.35);
      color: inherit;
    }
  </style>
  <div class="student-search">
    <label class="student-search__label" part="label"></label>
    <div class="student-search__control">
      <div class="student-search__input-wrapper">
        <input class="student-search__input" part="input" type="search" autocomplete="off" />
        <button class="student-search__clear" part="clear-button" type="button" aria-label="Limpiar búsqueda">&times;</button>
      </div>
      <div class="student-search__dropdown" part="dropdown" role="listbox"></div>
    </div>
    <p class="student-search__helper" part="helper" hidden></p>
  </div>
`;

const KEY = {
  ENTER: 'Enter',
  ESC: 'Escape',
  TAB: 'Tab',
  ARROW_DOWN: 'ArrowDown',
  ARROW_UP: 'ArrowUp'
};

const DEFAULTS = {
  label: 'Alumno',
  placeholder: 'Buscar alumno...',
  helper: '',
  minChars: 2,
  debounce: 300,
  labelKey: 'label',
  secondaryKey: 'subtitle',
  valueKey: 'value',
  searchParam: 'q'
};

const resolvePath = (object, path, fallback = '') => {
  if (!object || !path) return fallback;
  return path.split('.').reduce((acc, key) => (acc && acc[key] !== undefined ? acc[key] : undefined), object) ?? fallback;
};

const debounce = (fn, wait) => {
  let timeout;
  return function debounced(...args) {
    clearTimeout(timeout);
    timeout = setTimeout(() => fn.apply(this, args), wait);
  };
};

class StudentSearch extends HTMLElement {
  static get observedAttributes() {
    return ['label', 'placeholder', 'helper-text', 'disabled', 'min-chars', 'debounce', 'data-fetch-url', 'label-key', 'value-key', 'secondary-key', 'search-param'];
  }

  constructor() {
    super();
    this.attachShadow({ mode: 'open' }).appendChild(template.content.cloneNode(true));

    this._labelEl = this.shadowRoot.querySelector('.student-search__label');
    this._input = this.shadowRoot.querySelector('.student-search__input');
    this._dropdown = this.shadowRoot.querySelector('.student-search__dropdown');
    this._helper = this.shadowRoot.querySelector('.student-search__helper');
    this._clearBtn = this.shadowRoot.querySelector('.student-search__clear');

    this._staticData = [];
    this._currentResults = [];
    this._highlightedIndex = -1;
    this._selectedItem = null;
    this._loading = false;

    this._minChars = DEFAULTS.minChars;
    this._debounce = DEFAULTS.debounce;
    this._fetchUrl = null;
    this._labelKey = DEFAULTS.labelKey;
    this._secondaryKey = DEFAULTS.secondaryKey;
    this._valueKey = DEFAULTS.valueKey;
    this._searchParam = DEFAULTS.searchParam;

    this._handleInput = this._handleInput.bind(this);
    this._handleKeydown = this._handleKeydown.bind(this);
    this._handleOutsideClick = this._handleOutsideClick.bind(this);
    this._handleClear = this._handleClear.bind(this);
    this._debouncedRemoteSearch = debounce((query) => this._fetchRemote(query), this._debounce);
  }

  connectedCallback() {
    this._upgradeProperty('data');
    this._upgradeProperty('value');

    const inputId = this.getAttribute('input-id') || `student-search-${Math.random().toString(36).slice(2)}`;
    this._input.id = inputId;
    this._labelEl.setAttribute('for', inputId);

    this._applyInitialAttributes();

    this._input.addEventListener('input', this._handleInput);
    this._input.addEventListener('keydown', this._handleKeydown);
    this._clearBtn.addEventListener('click', this._handleClear);
    document.addEventListener('click', this._handleOutsideClick);
    this._syncClearVisibility();
  }

  disconnectedCallback() {
    this._input.removeEventListener('input', this._handleInput);
    this._input.removeEventListener('keydown', this._handleKeydown);
    this._clearBtn.removeEventListener('click', this._handleClear);
    document.removeEventListener('click', this._handleOutsideClick);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if (oldValue === newValue) return;

    switch (name) {
      case 'label':
        this._labelEl.textContent = newValue || DEFAULTS.label;
        this._labelEl.toggleAttribute('hidden', !newValue);
        break;
      case 'placeholder':
        this._input.placeholder = newValue || DEFAULTS.placeholder;
        break;
      case 'helper-text':
        this._helper.textContent = newValue || '';
        this._helper.toggleAttribute('hidden', !newValue);
        break;
      case 'disabled':
        this._input.disabled = newValue !== null;
        break;
      case 'min-chars':
        this._minChars = Number(newValue) || DEFAULTS.minChars;
        break;
      case 'debounce':
        this._debounce = Number(newValue) || DEFAULTS.debounce;
        this._debouncedRemoteSearch = debounce((query) => this._fetchRemote(query), this._debounce);
        break;
      case 'data-fetch-url':
        this._fetchUrl = newValue || null;
        break;
      case 'label-key':
        this._labelKey = newValue || DEFAULTS.labelKey;
        break;
      case 'secondary-key':
        this._secondaryKey = newValue || DEFAULTS.secondaryKey;
        break;
      case 'value-key':
        this._valueKey = newValue || DEFAULTS.valueKey;
        break;
      case 'search-param':
        this._searchParam = newValue || DEFAULTS.searchParam;
        break;
      default:
        break;
    }
  }

  set data(items) {
    if (!Array.isArray(items)) {
      this._staticData = [];
      return;
    }
    this._staticData = items;
    if (!this._fetchUrl) {
      this._filterStatic(this._input.value);
    }
  }

  get data() {
    return this._staticData;
  }

  set value(val) {
    const match = this._currentResults.find((item) => this._resolveValue(item) === val)
      || this._staticData.find((item) => this._resolveValue(item) === val)
      || null;

    if (match) {
      this._selectItem(match, false);
    } else if (!val) {
      this._clearSelection(false);
    }
  }

  get value() {
    return this._selectedItem ? this._resolveValue(this._selectedItem) : '';
  }

  focus() {
    this._input.focus();
  }

  clear() {
    this._clearSelection(true);
  }

  _upgradeProperty(prop) {
    if (Object.prototype.hasOwnProperty.call(this, prop)) {
      const value = this[prop];
      delete this[prop];
      this[prop] = value;
    }
  }

  _applyInitialAttributes() {
    this.attributeChangedCallback('label', null, this.getAttribute('label'));
    this.attributeChangedCallback('placeholder', null, this.getAttribute('placeholder'));
    this.attributeChangedCallback('helper-text', null, this.getAttribute('helper-text'));
    this.attributeChangedCallback('disabled', null, this.getAttribute('disabled'));
    this.attributeChangedCallback('min-chars', null, this.getAttribute('min-chars'));
    this.attributeChangedCallback('debounce', null, this.getAttribute('debounce'));
    this.attributeChangedCallback('data-fetch-url', null, this.getAttribute('data-fetch-url'));
    this.attributeChangedCallback('label-key', null, this.getAttribute('label-key'));
    this.attributeChangedCallback('secondary-key', null, this.getAttribute('secondary-key'));
    this.attributeChangedCallback('value-key', null, this.getAttribute('value-key'));
    this.attributeChangedCallback('search-param', null, this.getAttribute('search-param'));

    if (!this.hasAttribute('label')) {
      this._labelEl.setAttribute('hidden', '');
    }
  }

  _handleInput(event) {
    const query = event.target.value.trim();

    if (!query) {
      this._clearResults();
      this._dispatchQueryChange('');
      return;
    }

    this._dispatchQueryChange(query);

    if (query.length < this._minChars) {
      this._renderMessage(`Ingresa al menos ${this._minChars} caracteres para buscar.`);
      return;
    }

    if (this._fetchUrl) {
      this._debouncedRemoteSearch(query);
    } else {
      this._filterStatic(query);
    }
  }

  _dispatchQueryChange(query) {
    this.dispatchEvent(new CustomEvent('student-search:query', {
      detail: { query },
      bubbles: true,
      composed: true
    }));
  }

  async _fetchRemote(query) {
    if (!this._fetchUrl) return;

    this._setLoading(true);

    try {
      const url = new URL(this._fetchUrl, window.location.origin);
      if (this._searchParam) {
        url.searchParams.set(this._searchParam, query);
      } else {
        url.searchParams.set(DEFAULTS.searchParam, query);
      }

      const response = await fetch(url.toString(), {
        headers: { 'Accept': 'application/json' }
      });

      if (!response.ok) {
        throw new Error(`Request failed with status ${response.status}`);
      }

      const payload = await response.json();
      const results = Array.isArray(payload) ? payload : (payload?.data ?? []);

      this._currentResults = results;
      this._renderResults(results, query);
    } catch (error) {
      console.error('[student-search] Error fetching results', error);
      this._renderMessage('No pudimos obtener la información. Intenta nuevamente.', true);
    } finally {
      this._setLoading(false);
    }
  }

  _filterStatic(query) {
    if (!query) {
      this._clearResults();
      return;
    }

    const normalized = query.toLowerCase();
    const results = this._staticData.filter((item) =>
      String(this._resolveLabel(item)).toLowerCase().includes(normalized)
      || String(this._resolveSecondary(item)).toLowerCase().includes(normalized)
    );

    this._currentResults = results;
    this._renderResults(results, query);
  }

  _renderResults(results, query) {
    if (!results || !results.length) {
      this._renderMessage(`No encontramos resultados para "${query}".`);
      return;
    }

    this._dropdown.innerHTML = '';

    const list = document.createElement('div');
    list.className = 'student-search__results';

    results.forEach((item, index) => {
      const option = document.createElement('button');
      option.type = 'button';
      option.className = 'student-search__option';
      option.dataset.index = String(index);
      option.setAttribute('role', 'option');
      option.setAttribute('aria-selected', 'false');

      const primary = document.createElement('span');
      primary.className = 'student-search__option-primary';
      primary.textContent = this._resolveLabel(item);

      const secondaryText = this._resolveSecondary(item);
      if (secondaryText) {
        const secondary = document.createElement('span');
        secondary.className = 'student-search__option-secondary';
        secondary.textContent = secondaryText;
        option.append(primary, secondary);
      } else {
        option.append(primary);
      }

      option.addEventListener('click', () => this._selectItem(item, true));
      list.append(option);
    });

    this._dropdown.append(list);
    this._highlightedIndex = -1;
    this._openDropdown();
    this._syncClearVisibility();
  }

  _renderMessage(message, isError = false) {
    this._dropdown.innerHTML = '';
    this._currentResults = [];

    if (!message) {
      this._closeDropdown();
      return;
    }

    const container = document.createElement('div');
    container.className = 'student-search__message';
    if (isError) {
      container.classList.add('student-search__message--error');
    }
    container.textContent = message;
    this._dropdown.append(container);
    this._highlightedIndex = -1;
    this._openDropdown();
    this._syncClearVisibility();
  }

  _clearResults() {
    this._dropdown.innerHTML = '';
    this._currentResults = [];
    this._closeDropdown();
    this._highlightedIndex = -1;
    this._syncClearVisibility();
  }

  _setLoading(isLoading) {
    if (this._loading === isLoading) return;
    this._loading = isLoading;

    if (isLoading) {
      this._dropdown.innerHTML = '';
      const spinner = document.createElement('div');
      spinner.className = 'student-search__spinner';
      const wrapper = document.createElement('div');
      wrapper.className = 'student-search__message';
      wrapper.append(spinner);
      this._dropdown.append(wrapper);
      this._openDropdown();
    } else if (!this._currentResults.length && !this._dropdown.childElementCount) {
      this._closeDropdown();
    }
  }

  _openDropdown() {
    this._dropdown.classList.add('student-search__dropdown--open');
  }

  _closeDropdown() {
    this._dropdown.classList.remove('student-search__dropdown--open');
  }

  _handleKeydown(event) {
    switch (event.key) {
      case KEY.ARROW_DOWN:
        event.preventDefault();
        this._highlightNext();
        break;
      case KEY.ARROW_UP:
        event.preventDefault();
        this._highlightPrevious();
        break;
      case KEY.ENTER:
        if (this._highlightedIndex >= 0) {
          event.preventDefault();
          const item = this._currentResults[this._highlightedIndex];
          if (item) this._selectItem(item, true);
        }
        break;
      case KEY.ESC:
        this._closeDropdown();
        break;
      default:
        break;
    }
  }

  _highlightNext() {
    if (!this._currentResults.length) return;
    const nextIndex = this._highlightedIndex + 1 >= this._currentResults.length ? 0 : this._highlightedIndex + 1;
    this._highlightOption(nextIndex);
  }

  _highlightPrevious() {
    if (!this._currentResults.length) return;
    const prevIndex = this._highlightedIndex - 1 < 0 ? this._currentResults.length - 1 : this._highlightedIndex - 1;
    this._highlightOption(prevIndex);
  }

  _highlightOption(index) {
    const options = Array.from(this._dropdown.querySelectorAll('.student-search__option'));
    if (!options.length) return;

    options.forEach((option) => {
      option.classList.remove('student-search__option--active');
      option.setAttribute('aria-selected', 'false');
    });

    const optionToHighlight = options[index];
    if (!optionToHighlight) return;

    optionToHighlight.classList.add('student-search__option--active');
    optionToHighlight.setAttribute('aria-selected', 'true');
    this._highlightedIndex = index;
    optionToHighlight.scrollIntoView({ block: 'nearest' });
  }

  _selectItem(item, notify = true) {
    this._selectedItem = item;
    this._input.value = this._resolveLabel(item);
    this._closeDropdown();
    this._syncClearVisibility();

    if (notify) {
      this.dispatchEvent(new CustomEvent('student-search:select', {
        detail: {
          value: this._resolveValue(item),
          label: this._resolveLabel(item),
          item
        },
        bubbles: true,
        composed: true
      }));
    }
  }

  _clearSelection(notify = true) {
    this._selectedItem = null;
    this._input.value = '';
    this._currentResults = [];
    this._clearResults();
    this._syncClearVisibility();

    if (notify) {
      this.dispatchEvent(new CustomEvent('student-search:clear', {
        bubbles: true,
        composed: true
      }));
    }
  }

  _handleOutsideClick(event) {
    if (!this.isConnected) return;
    if (this.contains(event.target)) return;
    if (event.composedPath().includes(this)) return;
    this._closeDropdown();
  }

  _handleClear() {
    this._clearSelection(true);
    this._input.focus();
  }

  _syncClearVisibility() {
    const shouldShow = Boolean(this._input.value || this._selectedItem);
    this._clearBtn.toggleAttribute('hidden', !shouldShow);
  }

  _resolveLabel(item) {
    return resolvePath(item, this._labelKey, '') || '';
  }

  _resolveSecondary(item) {
    return resolvePath(item, this._secondaryKey, '');
  }

  _resolveValue(item) {
    return resolvePath(item, this._valueKey, '');
  }
}

if (!customElements.get('student-search')) {
  customElements.define('student-search', StudentSearch);
}

export default StudentSearch;
