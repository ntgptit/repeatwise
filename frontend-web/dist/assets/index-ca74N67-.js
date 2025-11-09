import{z as x,E as u,G as m,H as f,r as p,j as e,s as a,a as s,T as n,B as c,C as g,b8 as j}from"./index-Cg5IDLAj.js";import{P as v}from"./PageHelmet-DXTSFW23.js";import{G as l}from"./Grid-DHwwlgrX.js";import{C as h}from"./Container-CmgLE638.js";var t={},d;function y(){if(d)return t;d=1;var r=x();Object.defineProperty(t,"__esModule",{value:!0}),t.default=void 0;var i=r(u()),o=m();return t.default=(0,i.default)((0,o.jsx)("path",{d:"M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4z"}),"RefreshTwoTone"),t}var T=y();const w=f(T),R=a(l)(({theme:r})=>`
    background: ${r.colors.gradients.black1};
`),C=a(s)(()=>`
    height: 100%;
    display: flex;
    flex: 1;
    overflow: auto;
    flex-direction: column;
    align-items: center;
    justify-content: center;
`),b=a(n)(({theme:r})=>`
      color: ${r.colors.alpha.white[100]};
`),I=a(n)(({theme:r})=>`
      color: ${r.colors.alpha.white[70]};
`);function W(){const[r,i]=p.useState(!1);function o(){i(!0)}return e.jsxs(e.Fragment,{children:[e.jsx(v,{title:"Status - 500"}),e.jsx(C,{children:e.jsxs(l,{container:!0,sx:{height:"100%"},alignItems:"stretch",spacing:0,children:[e.jsx(l,{xs:12,md:6,alignItems:"center",display:"flex",justifyContent:"center",item:!0,children:e.jsx(h,{maxWidth:"sm",children:e.jsxs(s,{textAlign:"center",children:[e.jsx("img",{alt:"500",height:260,src:"/static/images/status/500.svg"}),e.jsx(n,{variant:"h2",sx:{my:2},children:"There was an error, please try again later"}),e.jsx(n,{variant:"h4",color:"text.secondary",fontWeight:"normal",sx:{mb:4},children:"The server encountered an internal error and was not able to complete your request"}),e.jsx(c,{onClick:o,variant:"outlined",color:"primary",startIcon:r?void 0:e.jsx(w,{}),disabled:r,sx:{mr:1},children:r?e.jsxs(s,{display:"flex",alignItems:"center",children:[e.jsx(g,{size:18,color:"inherit",sx:{mr:1}}),"Refreshing..."]}):"Refresh view"}),e.jsx(c,{href:"/overview",variant:"contained",sx:{ml:1},children:"Go back"})]})})}),e.jsx(j,{mdDown:!0,children:e.jsx(R,{xs:12,md:6,alignItems:"center",display:"flex",justifyContent:"center",item:!0,children:e.jsx(h,{maxWidth:"sm",children:e.jsxs(s,{textAlign:"center",children:[e.jsx(b,{variant:"h1",sx:{my:2},children:"Tokyo Free White React Typescript Admin Dashboard"}),e.jsx(I,{variant:"h4",fontWeight:"normal",sx:{mb:4},children:"High performance React template built with lots of powerful Material-UI components across multiple product niches for fast & perfect apps development processes."}),e.jsx(c,{href:"/overview",size:"large",variant:"contained",children:"Overview"})]})})})})]})})]})}export{W as default};
