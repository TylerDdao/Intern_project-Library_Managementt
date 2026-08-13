export function arraysHaveSameElements<T>(a: T[], b: T[], key: keyof T): boolean {
  if (a.length !== b.length) return false;
  const setA = new Set(a.map(item => item[key]));
  const setB = new Set(b.map(item => item[key]));
  if (setA.size !== setB.size) return false;
  return [...setA].every(val => setB.has(val));
}