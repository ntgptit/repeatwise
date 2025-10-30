// @ts-check
const js = require('@eslint/js')
const globals = require('globals')
const react = require('eslint-plugin-react')
const reactHooks = require('eslint-plugin-react-hooks')
const prettier = require('eslint-config-prettier')
const tseslint = require('typescript-eslint')

module.exports = tseslint.config(
  // Ignore patterns
  {
    ignores: [
      'dist',
      'node_modules',
      'build',
      '.metro',
      'android',
      'ios',
      '__tests__',
      'coverage',
      '.bundle',
      '*.config.js',
      '*.config.ts',
    ],
  },

  // Base configs
  js.configs.recommended,
  ...tseslint.configs.recommended,

  // Prettier config (disables conflicting ESLint rules)
  prettier,

  // Block JavaScript files in src/ - TypeScript only
  {
    files: ['src/**/*.{js,jsx}', 'App.{js,jsx}'],
    rules: {
      'no-restricted-syntax': [
        'error',
        {
          message:
            'TypeScript is required. Please use .ts/.tsx files instead of .js/.jsx files.',
          selector: 'Program',
        },
      ],
    },
  },

  // React Native configs
  {
    files: ['**/*.{js,jsx,ts,tsx}'],
    plugins: {
      react,
      'react-hooks': reactHooks,
    },
    languageOptions: {
      ecmaVersion: 2022,
      sourceType: 'module',
      globals: {
        ...globals.browser,
        ...globals.node,
        ...globals.es2021,
        // React Native globals
        __DEV__: 'readonly',
        console: 'readonly',
        global: 'readonly',
        process: 'readonly',
      },
      parserOptions: {
        ecmaFeatures: {
          jsx: true,
        },
      },
    },
    settings: {
      react: {
        version: 'detect',
      },
    },
    rules: {
      // ==========================================
      // TypeScript Rules
      // ==========================================
      '@typescript-eslint/no-explicit-any': 'warn',
      '@typescript-eslint/no-unused-vars': [
        'warn',
        {
          argsIgnorePattern: '^_',
          varsIgnorePattern: '^_',
          caughtErrorsIgnorePattern: '^_',
        },
      ],
      '@typescript-eslint/explicit-function-return-type': 'off',
      '@typescript-eslint/explicit-module-boundary-types': 'off',
      '@typescript-eslint/no-non-null-assertion': 'warn',
      '@typescript-eslint/no-empty-function': 'warn',
      '@typescript-eslint/no-empty-interface': 'warn',
      '@typescript-eslint/no-non-null-asserted-optional-chain': 'error',
      '@typescript-eslint/prefer-as-const': 'error',
      '@typescript-eslint/no-var-requires': 'error',
      '@typescript-eslint/ban-ts-comment': [
        'warn',
        {
          'ts-expect-error': 'allow-with-description',
          'ts-ignore': 'allow-with-description',
          'ts-nocheck': 'allow-with-description',
          'ts-check': false,
        },
      ],

      // ==========================================
      // React Rules
      // ==========================================
      ...react.configs.recommended.rules,
      ...react.configs['jsx-runtime'].rules,
      'react/react-in-jsx-scope': 'off', // Not needed with React 17+
      'react/prop-types': 'off', // Using TypeScript instead
      'react/display-name': 'off',
      'react/jsx-uses-react': 'off', // Not needed with React 17+
      'react/jsx-uses-vars': 'error',
      'react/jsx-key': ['error', { checkFragmentShorthand: true }],
      'react/jsx-no-duplicate-props': 'error',
      'react/jsx-no-undef': 'error',
      'react/jsx-pascal-case': ['error', { allowAllCaps: true }],
      'react/no-array-index-key': 'warn',
      'react/no-children-prop': 'error',
      'react/no-danger': 'warn',
      'react/no-deprecated': 'warn',
      'react/no-direct-mutation-state': 'error',
      'react/no-find-dom-node': 'error',
      'react/no-is-mounted': 'error',
      'react/no-render-return-value': 'error',
      'react/no-string-refs': 'error',
      'react/no-unescaped-entities': 'error',
      'react/no-unknown-property': 'error',
      'react/require-render-return': 'error',
      'react/self-closing-comp': ['error', { component: true, html: true }],
      'react/jsx-boolean-value': ['error', 'never'],
      'react/jsx-curly-brace-presence': ['error', { props: 'never', children: 'never' }],
      'react/jsx-fragments': ['error', 'syntax'],
      'react/jsx-no-leaked-render': 'error',
      'react/jsx-no-useless-fragment': 'error',

      // ==========================================
      // React Hooks Rules
      // ==========================================
      ...reactHooks.configs.recommended.rules,
      'react-hooks/rules-of-hooks': 'error',
      'react-hooks/exhaustive-deps': 'warn',

      // ==========================================
      // React Native Specific Rules
      // ==========================================
      // Note: eslint-plugin-react-native doesn't support ESLint 9.x yet
      // These rules are handled via custom checks or removed for now
      // You can add them back when the plugin is updated

      // ==========================================
      // General JavaScript/ES6 Rules
      // ==========================================
      'no-console': ['warn', { allow: ['warn', 'error'] }],
      'no-debugger': 'warn',
      'no-var': 'error',
      'prefer-const': 'error',
      'prefer-arrow-callback': 'error',
      'prefer-template': 'error',
      'prefer-destructuring': ['warn', { object: true, array: false }],
      'object-shorthand': 'error',
      'no-unused-expressions': 'warn',
      'no-duplicate-imports': 'error',
      'no-useless-constructor': 'error',
      'no-useless-return': 'error',
      'no-return-await': 'error',
      'require-await': 'warn',
      'no-await-in-loop': 'warn',
      'prefer-promise-reject-errors': 'error',
      'no-throw-literal': 'error',
      'no-return-assign': 'error',
      'no-implicit-coercion': 'warn',
      'eqeqeq': ['error', 'always', { null: 'ignore' }],
      'curly': ['error', 'all'],
      'default-case': 'warn',
      'default-case-last': 'error',
      'no-else-return': ['warn', { allowElseIf: false }],
      'no-lonely-if': 'error',
      'no-unneeded-ternary': 'error',
      'prefer-exponentiation-operator': 'error',
      'yoda': 'error',
    },
  },

  // Override for config files
  {
    files: ['*.config.{js,ts}', 'metro.config.js', 'babel.config.js'],
    rules: {
      'no-console': 'off',
      '@typescript-eslint/no-var-requires': 'off',
    },
  },

  // Override for test files
  {
    files: ['**/*.{test,spec}.{js,jsx,ts,tsx}'],
    rules: {
      'no-console': 'off',
    },
  },
)