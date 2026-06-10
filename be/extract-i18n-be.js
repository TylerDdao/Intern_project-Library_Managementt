// extract-i18n-backend.js
const fs = require('fs');
const path = require('path');

const SRC_DIR = './src/main/java';
const OUTPUT_DIR = './src/main/resources/i18n';
const MESSAGES_FILE = path.join(OUTPUT_DIR, 'messages.properties');

// Match messageSource.getMessage("some.key", ...)
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
const existing = loadExisting(MESSAGES_FILE);

// Report missing keys
const missing = [...foundKeys].filter(k => !existing[k]);
if (missing.length) {
    console.log('Missing keys:');
    missing.forEach(k => console.log(`  ${k}`));
} else {
    console.log('All keys are present.');
}

// Merge and write — existing values preserved, missing keys added as TODO
const merged = { ...existing };
for (const key of foundKeys) {
    if (!merged[key]) merged[key] = 'TODO';
}

if (!fs.existsSync(OUTPUT_DIR)) fs.mkdirSync(OUTPUT_DIR, { recursive: true });
const output = Object.entries(merged).map(([k, v]) => `${k}=${v}`).join('\n');
fs.writeFileSync(MESSAGES_FILE, output, 'utf-8');
console.log(`Written to ${MESSAGES_FILE}`);