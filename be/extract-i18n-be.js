// extract-i18n-backend.js
const fs = require('fs');
const path = require('path');

const SRC_DIR = './src/main/java';
const OUTPUT_DIR = './src/main/resources/i18n';
const LOCALES = ['messages', 'messages_vi', 'messages_fr']; // messages = English (default)

const KEY_REGEX = /messageSource\.getMessage\("([^"]+)"/g;

function scanDir(dir) {
    const keys = new Set();
    const files = fs.readdirSync(dir, { withFileTypes: true });

    for (const file of files) {
        const fullPath = path.join(dir, file.name);
        if (file.isDirectory()) {
            scanDir(fullPath).forEach(k => keys.add(k));
        } else if (file.name.endsWith('.java')) {
            const content = fs.readFileSync(fullPath, 'utf-8');
            let match;
            while ((match = KEY_REGEX.exec(content)) !== null) {
                keys.add(match[1]);
            }
        }
    }
    return keys;
}

function loadExisting(filePath) {
    if (!fs.existsSync(filePath)) return {};
    const lines = fs.readFileSync(filePath, 'utf-8').split('\n');
    const result = {};
    for (const line of lines) {
        const [key, ...rest] = line.split('=');
        if (key && rest.length) result[key.trim()] = rest.join('=').trim();
    }
    return result;
}

const foundKeys = scanDir(SRC_DIR);

if (!fs.existsSync(OUTPUT_DIR)) fs.mkdirSync(OUTPUT_DIR, { recursive: true });

for (const locale of LOCALES) {
    const filePath = path.join(OUTPUT_DIR, `${locale}.properties`);
    const existing = loadExisting(filePath);

    const missing = [...foundKeys].filter(k => !existing[k]);
    if (missing.length) {
        console.log(`[${locale}] Missing ${missing.length} keys:`);
        missing.forEach(k => console.log(`  ${k}`));
    } else {
        console.log(`[${locale}] All keys are present.`);
    }

    const merged = { ...existing };
    for (const key of foundKeys) {
        if (!merged[key]) merged[key] = 'TODO';
    }

    const output = Object.entries(merged).map(([k, v]) => `${k}=${v}`).join('\n');
    fs.writeFileSync(filePath, output, 'utf-8');
    console.log(`[${locale}] Written to ${filePath}`);
}