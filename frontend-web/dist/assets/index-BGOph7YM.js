import{z as g,E as y,G as T,H as v,r as p,j as e,a as r,s as i,b as w,T as c,F as b,I as f,bc as M,D as I,f as u,e as x,b7 as C,B as D}from"./index-Cg5IDLAj.js";import{F,T as S,I as E}from"./Instagram-CwIRaJG2.js";import{P as H}from"./PageHelmet-DXTSFW23.js";import{C as h}from"./Container-CmgLE638.js";var a={},j;function W(){if(j)return a;j=1;var t=g();Object.defineProperty(a,"__esModule",{value:!0}),a.default=void 0;var o=t(y()),l=T();return a.default=(0,o.default)([(0,l.jsx)("path",{d:"M20 6H4l8 4.99zM4 8v10h16V8l-8 5z",opacity:".3"},"0"),(0,l.jsx)("path",{d:"M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2m0 2-8 4.99L4 6zm0 12H4V8l8 5 8-5z"},"1")],"MailTwoTone"),a}var q=W();const z=v(q),A=i(r)(()=>`
    height: 100%;
    display: flex;
    flex: 1;
    overflow: auto;
    flex-direction: column;
    align-items: center;
    justify-content: center;
`),R=i(c)(({theme:t})=>`
  font-size: ${t.typography.pxToRem(75)};
`),k=i(c)(({theme:t})=>`
  color: ${t.colors.alpha.black[50]};
`),L=i(C)(({theme:t})=>`
    background-color: ${t.colors.alpha.white[100]};
`),B=i(D)(({theme:t})=>`
    margin-right: -${t.spacing(1)};
`);function P(){const t=()=>{const s=Number(new Date("2023-12-31T00:00:00Z"))-Number(new Date);let d={};return s>0&&(d={days:Math.floor(s/864e5),hours:Math.floor(s/36e5%24),minutes:Math.floor(s/1e3/60%60),seconds:Math.floor(s/1e3%60)}),d},[o,l]=p.useState(t());p.useEffect(()=>{setTimeout(()=>{l(t())},1e3)});const m=[];return Object.keys(o).forEach(n=>{o[n]&&m.push(e.jsxs(r,{textAlign:"center",px:3,children:[e.jsx(R,{variant:"h1",children:o[n]}),e.jsx(k,{variant:"h3",children:n})]},n))}),e.jsxs(e.Fragment,{children:[e.jsx(H,{title:"Status - Coming Soon"}),e.jsx(A,{children:e.jsxs(h,{maxWidth:"md",children:[e.jsx(w,{}),e.jsxs(r,{textAlign:"center",mb:3,children:[e.jsxs(h,{maxWidth:"xs",children:[e.jsx(c,{variant:"h1",sx:{mt:4,mb:2},children:"Coming Soon"}),e.jsx(c,{variant:"h3",color:"text.secondary",fontWeight:"normal",sx:{mb:4},children:"We’re working on implementing the last features before our launch!"})]}),e.jsx("img",{alt:"Coming Soon",height:200,src:"/static/images/status/coming-soon.svg"})]}),e.jsx(r,{display:"flex",justifyContent:"center",children:m.length?m:e.jsx(e.Fragment,{children:"Time's up!"})}),e.jsx(h,{maxWidth:"sm",children:e.jsxs(r,{sx:{textAlign:"center",p:4},children:[e.jsxs(b,{variant:"outlined",fullWidth:!0,children:[e.jsx(L,{type:"text",placeholder:"Enter your email address here...",endAdornment:e.jsx(f,{position:"end",children:e.jsx(B,{variant:"contained",size:"small",children:"Notify Me"})}),startAdornment:e.jsx(f,{position:"start",children:e.jsx(z,{})})}),e.jsx(M,{children:"We’ll email you once our website is launched!"})]}),e.jsx(I,{sx:{my:4}}),e.jsxs(r,{sx:{textAlign:"center"},children:[e.jsx(u,{arrow:!0,placement:"top",title:"Facebook",children:e.jsx(x,{color:"primary",children:e.jsx(F,{})})}),e.jsx(u,{arrow:!0,placement:"top",title:"Twitter",children:e.jsx(x,{color:"primary",children:e.jsx(S,{})})}),e.jsx(u,{arrow:!0,placement:"top",title:"Instagram",children:e.jsx(x,{color:"primary",children:e.jsx(E,{})})})]})]})})]})})]})}export{P as default};
