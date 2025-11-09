import{j as r,s as e,a as t,f as n,L as i}from"./index-Cg5IDLAj.js";const a=e(i)(({theme:o})=>`
        color: ${o.palette.text.primary};
        padding: ${o.spacing(0,1,0,0)};
        display: flex;
        text-decoration: none;
        font-weight: ${o.typography.fontWeightBold};
`),s=e(t)(()=>`
        width: 52px;
        height: 38px;
        margin-top: 4px;
        transform: scale(.8);
`),p=e(t)(({theme:o})=>`
        background: ${o.general.reactFrameworkColor};
        width: 18px;
        height: 18px;
        border-radius: ${o.general.borderRadiusSm};
        position: relative;
        transform: rotate(45deg);
        top: 3px;
        left: 17px;

        &:after, 
        &:before {
            content: "";
            display: block;
            width: 18px;
            height: 18px;
            position: absolute;
            top: -1px;
            right: -20px;
            transform: rotate(0deg);
            border-radius: ${o.general.borderRadiusSm};
        }

        &:before {
            background: ${o.palette.primary.main};
            right: auto;
            left: 0;
            top: 20px;
        }

        &:after {
            background: ${o.palette.secondary.main};
        }
`),d=e(t)(({theme:o})=>`
        width: 16px;
        height: 16px;
        position: absolute;
        top: 12px;
        left: 12px;
        z-index: 5;
        border-radius: ${o.general.borderRadiusSm};
        background: ${o.header.background};
`),g=e(t)(({theme:o})=>`
        padding-left: ${o.spacing(1)};
`),l=e(t)(({theme:o})=>`
        background: ${o.palette.success.main};
        color: ${o.palette.success.contrastText};
        padding: ${o.spacing(.4,1)};
        border-radius: ${o.general.borderRadiusSm};
        text-align: center;
        display: inline-block;
        line-height: 1;
        font-size: ${o.typography.pxToRem(11)};
`),c=e(t)(({theme:o})=>`
        font-size: ${o.typography.pxToRem(15)};
        font-weight: ${o.typography.fontWeightBold};
`);function h(){return r.jsxs(a,{to:"/overview",children:[r.jsx(s,{children:r.jsx(p,{children:r.jsx(d,{})})}),r.jsx(t,{component:"span",sx:{display:{xs:"none",sm:"inline-block"}},children:r.jsxs(g,{children:[r.jsx(n,{title:"Version 2.0",arrow:!0,placement:"right",children:r.jsx(l,{children:"3.1"})}),r.jsx(c,{children:"Tokyo Free White"})]})})]})}export{h as L};
