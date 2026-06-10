// extract-i18n-frontend.js
const fs = require('fs');
const path = require('path');

const SRC_DIR = './src';
const LOCALES_DIR = './src/assets/i18n';
const LOCALES = ['en', 'vi', 'fr']; // add more as needed

// Match translate.instant('key') or translate.get('key') or | translate in templates
const TS_REGEX = /translate\.(?:instant|get)\(['"`]([^'"`]+)['"`]\)/g;
const HTML_REGEX = /['"`]([^'"`]+)['"`]\s*\|\s*translate/g;
const TS_MARKER_REGEX = /['"`]([^'"`]+)['"`].*\/\/\s*i18n/g;

function scanDir(dir) {
    const keys = new Set();
    const files = fs.readdirSync(dir, { withFileTypes: true });

    for (const file of files) {
        const fullPath = path.join(dir, file.name);
        if (file.isDirectory()) {
            scanDir(fullPath).forEach(k => keys.add(k));
        } else if (file.name.endsWith('.ts') || file.name.endsWith('.html')) {
            const content = fs.readFileSync(fullPath, 'utf-8');
            const regex = file.name.endsWith('.html') ? HTML_REGEX : TS_REGEX;
            let match;
            while ((match = regex.exec(content)) !== null) {
                keys.add(match[1]);
            }
        }
    }
    return keys;
}

// Flatten nested JSON keys → "SECTION.KEY"
function flattenKeys(obj, prefix = '') {
    return Object.entries(obj).reduce((acc, [k, v]) => {
        const fullKey = prefix ? `${prefix}.${k}` : k;
        if (typeof v === 'object' && v !== null) {
            Object.assign(acc, flattenKeys(v, fullKey));
        } else {
            acc[fullKey] = v;
        }
        return acc;
    }, {});
}

// Rebuild nested JSON from flat keys
function unflattenKeys(flat) {
    const result = {};
    for (const [key, value] of Object.entries(flat)) {
        const parts = key.split('.');
        let current = result;
        for (let i = 0; i < parts.length - 1; i++) {
            if (!current[parts[i]]) current[parts[i]] = {};
            current = current[parts[i]];
        }
        current[parts[parts.length - 1]] = value;
    }
    return result;
}

const foundKeys = scanDir(SRC_DIR);
console.log(`Found ${foundKeys.size} keys\n`);

for (const locale of LOCALES) {
    const filePath = path.join(LOCALES_DIR, `${locale}.json`);
    const existing = fs.existsSync(filePath)
        ? flattenKeys(JSON.parse(fs.readFileSync(filePath, 'utf-8')))
        : {};

    // Report missing
    const missing = [...foundKeys].filter(k => !existing[k]);
    if (missing.length) {
        console.log(`[${locale}] Missing ${missing.length} keys:`);
        missing.forEach(k => console.log(`  ${k}`));
    } else {
        console.log(`[${locale}] All keys present`);
    }

    // Report unused (in JSON but not in source)
    const unused = Object.keys(existing).filter(k => !foundKeys.has(k));
    if (unused.length) {
        console.log(`[${locale}] Unused ${unused.length} keys:`);
        unused.forEach(k => console.log(`  ${k}`));
    }

    // Merge — preserve existing, add missing as TODO
    const merged = { ...existing };
    for (const key of foundKeys) {
        if (!merged[key]) merged[key] = 'TODO';
    }

    const nested = unflattenKeys(merged);
    fs.writeFileSync(filePath, JSON.stringify(nested, null, 2), 'utf-8');
    console.log(`[${locale}] Written to ${filePath}\n`);
}